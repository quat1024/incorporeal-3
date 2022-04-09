package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.entity.CorporeaPylonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.BlockMod;

import java.util.Locale;

/**
 * A three-block tall multiblock structure that can accept Corporea Sparks.
 * Two sparked structures can be bound together using the Wand of the Forest.
 * Then, the Corporea Sparks on the pylons are considered connected, regardless of proximity.
 */
public class CorporeaPylonBlock extends BlockMod implements EntityBlock {
	public CorporeaPylonBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState().setValue(WHICH, Which.BOTTOM));
	}
	
	public static final EnumProperty<Which> WHICH = EnumProperty.create("which", Which.class);
	
	private static final VoxelShape TOP_SHAPE    = box(6, 0, 6, 10, 16, 10);
	private static final VoxelShape MIDDLE_SHAPE = box(4, 0, 4, 12, 16, 12);
	private static final VoxelShape BOTTOM_SHAPE = box(2, 0, 2, 14, 16, 14);
	
	public enum Which implements StringRepresentable {
		TOP, MIDDLE, BOTTOM;
		@Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(WHICH));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch(state.getValue(WHICH)) {
			case TOP    -> TOP_SHAPE;
			case MIDDLE -> MIDDLE_SHAPE;
			case BOTTOM -> BOTTOM_SHAPE;
		};
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction side, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		if(true) return super.updateShape(state, side, facingState, level, pos, facingPos);
		if(!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
		
		BlockState top, middle, bottom;
		switch(state.getValue(WHICH)) {
			case TOP -> {
				top = state;
				middle = level.getBlockState(pos.below(1));
				bottom = level.getBlockState(pos.below(2));
			}
			case MIDDLE -> {
				top = level.getBlockState(pos.above(1));
				middle = state;
				bottom = level.getBlockState(pos.below(1));
			}
			case BOTTOM -> {
				top = level.getBlockState(pos.above(2));
				middle = level.getBlockState(pos.above(1));
				bottom = state;
			}
			//without this, java says top/middle/bottom "might not have been initialized"
			//even though the point of switch expressions is to force all cases to be covered
			//reh!
			default -> throw new IllegalStateException();
		}
		
		if(top.is(this) && top.getValue(WHICH) == Which.TOP &&
			middle.is(this) && middle.getValue(WHICH) == Which.MIDDLE &&
			bottom.is(this) && bottom.getValue(WHICH) == Which.BOTTOM) {
			return super.updateShape(state, side, facingState, level, pos, facingPos);
		} else {
			return Blocks.AIR.defaultBlockState();
		}
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		
		//ensure the structure will fit in the world height
		if(pos.above(2).getY() > level.getMaxBuildHeight()) return null;
		//ensure there's clearance one block above
		if(!level.getBlockState(pos.above(1)).canBeReplaced(ctx)) return null;
		//ensure there's clearance two blocks above
		if(!level.getBlockState(pos.above(2)).canBeReplaced(ctx)) return null;
		
		return defaultBlockState().setValue(WHICH, Which.BOTTOM);
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		//place the middle part one block above
		level.setBlockAndUpdate(pos.above(1), state.setValue(WHICH, Which.MIDDLE));
		//place the top part two blocks above
		level.setBlockAndUpdate(pos.above(2), state.setValue(WHICH, Which.TOP));
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState below = level.getBlockState(pos.below());
		return switch(state.getValue(WHICH)) {
			case TOP    -> below.is(this) && below.getValue(WHICH) == Which.MIDDLE;
			case MIDDLE -> below.is(this) && below.getValue(WHICH) == Which.BOTTOM;
			case BOTTOM -> true;
		};
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState $$0) {
		return PushReaction.DESTROY;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if(state.getValue(WHICH) == Which.BOTTOM) return IncBlockEntityTypes.CORPOREA_PYLON.create(pos, state);
		else return null;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if(state.getValue(WHICH) == Which.BOTTOM) return createTickerHelper(type, IncBlockEntityTypes.CORPOREA_PYLON, CorporeaPylonBlockEntity::tick);
		else return null;
	}
}
