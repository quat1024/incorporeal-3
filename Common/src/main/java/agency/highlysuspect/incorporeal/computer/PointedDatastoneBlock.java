package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.BlockModWaterloggable;

import java.util.Random;

/**
 * TODO OOOOOOOOOO, maybe let you place it on the floor? i guess?
 *  adds a LOT of complexity. i will need to work out TIP_MERGE stuff too
 */
public class PointedDatastoneBlock extends BlockModWaterloggable implements EntityBlock {
	public PointedDatastoneBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState()
			.setValue(THICKNESS, DripstoneThickness.TIP));
	}
	
	//public static final EnumProperty<DripstoneThickness> THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
	public static final EnumProperty<DripstoneThickness> THICKNESS = EnumProperty.create("thickness", DripstoneThickness.class, t -> t != DripstoneThickness.TIP_MERGE);
	
	//from PointedDripstoneBlock
	public static final VoxelShape TIP_SHAPE = Block.box(5, 5, 5, 11, 16, 11);
	public static final VoxelShape FRUSTUM_SHAPE = Block.box(4, 0, 4, 12, 16, 12);
	public static final VoxelShape MIDDLE_SHAPE = Block.box(3, 0, 3, 13, 16, 13);
	public static final VoxelShape BASE_SHAPE = Block.box(2, 0, 2, 14, 16, 14);
	
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch(state.getValue(THICKNESS)) {
			case TIP -> TIP_SHAPE;
			case FRUSTUM -> FRUSTUM_SHAPE;
			case MIDDLE -> MIDDLE_SHAPE;
			case BASE -> BASE_SHAPE;
			case TIP_MERGE -> Shapes.block(); //NYI
		};
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(THICKNESS));
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction side, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		state = super.updateShape(state, side, facingState, level, pos, facingPos);
		
		//ignore changes from sides that will never be relevant
		if(side != Direction.UP && side != Direction.DOWN) return state;
		
		//count how many dripstones are directly below me. Sorry for this loop btw
		BlockPos.MutableBlockPos cursor = pos.mutable().move(Direction.DOWN);
		int dripstonesBelowMe = 0;
		while(dripstonesBelowMe <= 3) { //dont care about any past 3, might as well be "a lot"
			if(level.getBlockState(cursor).getBlock() == this) {
				dripstonesBelowMe++;
				cursor.move(Direction.DOWN);
			} else break;
		}
		
		//guess my shape based on that
		//base -> middle middle middle middle -> frustum -> tip
		state = state.setValue(THICKNESS, switch(dripstonesBelowMe) {
			case 0 -> DripstoneThickness.TIP;
			case 1 -> DripstoneThickness.FRUSTUM;
			default -> DripstoneThickness.MIDDLE;
		});
		
		BlockState above = side == Direction.UP ? facingState : level.getBlockState(pos.above());
		//If I'm not under another dripstone:
		if(above.getBlock() != this) {
			//check that this block actually supports dripstone
			if(!canSurvive(state, level, pos) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
				level.scheduleTick(pos, this, 2);
			}
			
			//check if I should be a base
			if(dripstonesBelowMe >= 3) state = state.setValue(THICKNESS, DripstoneThickness.BASE);
		}
		
		return state;
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		//find the bottom of the stalactite
		BlockPos.MutableBlockPos cursor = pos.mutable();
		int count = 0;
		while(level.getBlockState(cursor).getBlock() == this) {
			count++;
			cursor.move(Direction.DOWN);
		}
		
		//moving upwards towards myself, create falling blocks along the way
		for(int i = 0; i < count; i++) {
			cursor.move(Direction.UP);
			fall(level, cursor);
		}
	}
	
	//Quick hack.
	//Retracting a pointed datastone column makes the bottom block turn into a falling block entity.
	//However, if the entity is immediately able to land and survive, it just looks like nothing happened.
	//I want you to be able to catch pointed datastone falling block entities, but I also want them to at least *try* to fall.
	public void fallOrBreak(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		//canSupportCenter is a poor approximation of this btw lol
		if(canSurvive(state, level, pos) && Block.canSupportCenter(level, pos.below(), Direction.UP)) level.destroyBlock(pos, true);
		else fall(level, pos, state);
	}
	
	public void fall(ServerLevel level, BlockPos pos) {
		fall(level, pos, level.getBlockState(pos));
	}
	
	public void fall(ServerLevel level, BlockPos pos, BlockState state) {
		CompoundTag tag = null;
		if(level.getBlockEntity(pos) instanceof PointedDatastoneBlockEntity be) {
			//did you know falling block entities can store nbt? now you do
			tag = be.saveWithoutMetadata();
		}
		
		FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, state);
		
		if(tag != null) {
			fallingBlock.blockData = tag;
		}
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos posAbove = pos.above();
		BlockState above = level.getBlockState(posAbove);
		
		if(above.getBlock() == this) return true;
		else return above.isFaceSturdy(level, posAbove, Direction.DOWN, SupportType.FULL);
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState $$0) {
		return PushReaction.DESTROY;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IncBlockEntityTypes.POINTED_DATASTONE.create(pos, state);
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState $$0) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if(level.getBlockEntity(pos) instanceof PointedDatastoneBlockEntity pdbe) return pdbe.signal();
		else return 0;
	}
}
