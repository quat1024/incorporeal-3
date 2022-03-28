package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.BlockModWaterloggable;

import java.util.Locale;
import java.util.Random;

/**
 * TODO OOOOOOOOOO, maybe let you place it on the floor? i guess?
 *  adds a LOT of complexity. i will need to work out TIP_MERGE stuff too
 */
public class PointedDatastoneBlock extends BlockModWaterloggable implements EntityBlock {
	public PointedDatastoneBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState()
			.setValue(TYPE, Type.ON_GROUND));
	}
	
	public boolean isHangingDatastone(BlockState state) {
		return state.getBlock() == this && state.getValue(TYPE) != Type.ON_GROUND;
	}
	
	//public static final EnumProperty<DripstoneThickness> THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
	//public static final EnumProperty<DripstoneThickness> THICKNESS = EnumProperty.create("thickness", DripstoneThickness.class, t -> t != DripstoneThickness.TIP_MERGE);
	public static final EnumProperty<Type> TYPE = EnumProperty.create("thickness", Type.class);
	
	public enum Type implements StringRepresentable {
		TIP(Block.box(5, 5, 5, 11, 16, 11)),
		FRUSTUM(Block.box(4, 0, 4, 12, 16, 12)),
		MIDDLE(Block.box(3, 0, 3, 13, 16, 13)),
		BASE(Block.box(2, 0, 2, 14, 16, 14)),
		ON_GROUND(Block.box(5, 0, 5, 11, 11, 11)) //TIP_SHAPE_UP
		;
		
		Type(VoxelShape shape) { this.shape = shape; }
		public final VoxelShape shape;
		
		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
		
		public Direction attachmentDirection() {
			if(this == ON_GROUND) return Direction.DOWN;
			else return Direction.UP;
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return state.getValue(TYPE).shape;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(TYPE));
	}
	
	@SuppressWarnings("NullableProblems") //BlockModWaterloggable marks this @Nonnull, which is incorrect
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(TYPE, context.getClickedFace() == Direction.DOWN ? Type.TIP : Type.ON_GROUND);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction side, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		state = super.updateShape(state, side, facingState, level, pos, facingPos);
		
		//ignore changes from sides that will never be relevant
		if(side != Direction.UP && side != Direction.DOWN) return state;
		
		//Lazy haxx for the upward pointing ones because i cba to rewrite this whole method with proper dripstone algorithm in place
		//:cowboy:
		if(!isHangingDatastone(state)) {
			//just do the canSurvive check and leave
			if(!canSurvive(state, level, pos) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
				level.scheduleTick(pos, this, 2);
			}
			return state;
		}
		
		//count how many dripstones are directly below me. Sorry for this loop btw
		BlockPos.MutableBlockPos cursor = pos.mutable().move(Direction.DOWN);
		int dripstonesBelowMe = 0;
		while(dripstonesBelowMe <= 3) { //dont care about any past 3, might as well be "a lot"
			if(isHangingDatastone(level.getBlockState(cursor))) {
				dripstonesBelowMe++;
				cursor.move(Direction.DOWN);
			} else break;
		}
		
		//guess my shape based on that
		//base -> middle middle middle middle -> frustum -> tip
		state = state.setValue(TYPE, switch(dripstonesBelowMe) {
			case 0 -> Type.TIP;
			case 1 -> Type.FRUSTUM;
			default -> Type.MIDDLE;
		});
		
		BlockState above = side == Direction.UP ? facingState : level.getBlockState(pos.above());
		//If I'm not under another dripstone:
		if(above.getBlock() != this) {
			//check that this block actually supports dripstone
			if(!canSurvive(state, level, pos) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
				level.scheduleTick(pos, this, 2);
			}
			
			//check if I should be a base
			if(dripstonesBelowMe >= 3) state = state.setValue(TYPE, Type.BASE);
		}
		
		return state;
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		//find the bottom of the stalactite
		BlockPos.MutableBlockPos cursor = pos.mutable();
		int count = 0;
		while(isHangingDatastone(level.getBlockState(cursor))) {
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
		if(level.getBlockEntity(pos) instanceof DataStorageBlockEntity be) {
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
		Direction attachDirection = state.getValue(TYPE).attachmentDirection();
		BlockPos checkPos = pos.relative(attachDirection, 1);
		BlockState checkState = level.getBlockState(checkPos);
		
		return isHangingDatastone(checkState) || checkState.isFaceSturdy(level, checkPos, attachDirection.getOpposite(), SupportType.FULL);
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
		if(level.getBlockEntity(pos) instanceof DataStorageBlockEntity storageBlockEntity) return storageBlockEntity.signal();
		else return 0;
	}
}
