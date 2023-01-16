package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.entity.CorporeaPylonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.BotaniaBlock;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A three-block tall multiblock structure that can accept Corporea Sparks.
 * Two sparked structures can be bound together using the Wand of the Forest.
 * Then, the Corporea Sparks on the pylons are considered connected, regardless of proximity.
 */
public class CorporeaPylonBlock extends BotaniaBlock implements EntityBlock {
	public CorporeaPylonBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState().setValue(WHICH, Which.BOTTOM));
	}
	
	public static final EnumProperty<Which> WHICH = EnumProperty.create("which", Which.class);
	
	private static final VoxelShape TOP_SHAPE    = makeTopShape();
	private static final VoxelShape MIDDLE_SHAPE = makeMiddleShape();
	private static final VoxelShape BOTTOM_SHAPE = makeBottomShape();
	
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
	
	//This is LARGELY based on Yttr's BigBlock infratructure, because I have no idea how to do multiblocks
	
	//Returns which BlockState is expected to be adjacent to myself in the given direction, or "null" if it doesn't matter.
	protected @Nullable BlockState expectedNeighbor(BlockState self, Direction dir) {
		if(dir == Direction.UP) return switch(self.getValue(WHICH)) {
			case TOP -> null;
			case MIDDLE -> self.setValue(WHICH, Which.TOP);
			case BOTTOM -> self.setValue(WHICH, Which.MIDDLE);
		};
		
		if(dir == Direction.DOWN) return switch(self.getValue(WHICH)) {
			case TOP -> self.setValue(WHICH, Which.MIDDLE);
			case MIDDLE -> self.setValue(WHICH, Which.BOTTOM);
			case BOTTOM -> null;
		};
		
		return null;
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction fromDir, BlockState fromState, LevelAccessor level, BlockPos pos, BlockPos fromPos) {
		//Check which blockstate is supposed to be adjacent to me from this direction
		@Nullable BlockState expectedNeighbor = expectedNeighbor(state, fromDir);
		//If the expected neighbor is irrelevant, then we're good
		if(expectedNeighbor == null) return state;
		//If the expected neighbor is incorrect, remove myself
		if(!Objects.equals(fromState, expectedNeighbor)) return Blocks.AIR.defaultBlockState();
		//If the expected neighbor is correct, then we're good too
		return state;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean moved) {
		super.neighborChanged(state, level, pos, fromBlock, fromPos, moved);
		if(level.getBlockTicks().hasScheduledTick(pos, state.getBlock())) level.scheduleTick(pos, state.getBlock(), 1);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		//Check that the multiblock is intact around myself
		for(Direction dir : List.of(Direction.UP, Direction.DOWN)) {
			BlockState expected = expectedNeighbor(state, dir);
			if(expected == null) continue;
			
			BlockState found = level.getBlockState(pos.relative(dir));
			if(!Objects.equals(expected, found)) {
				//break off in a way that causes block updates
				level.removeBlock(pos, false);
				return;
			}
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
	
	//based on that blockbench "export voxel shape" plugin lol
	private static VoxelShape makeTopShape() {
		VoxelShape shape = Shapes.empty();
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.375, 0, 0.375, 0.625, 0.5625, 0.625), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.625, 0.75, 0.25, 0.75, 1.0625, 0.375), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.625, 0.625, 0.625, 0.75, 0.9375, 0.75), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.75, 0.625, 0.375, 1.0625, 0.75), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.625, 0.25, 0.375, 0.9375, 0.375), BooleanOp.OR);
		
		return shape.optimize();
	}
	
	private static VoxelShape makeMiddleShape() {
		VoxelShape shape = Shapes.empty();
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0, 0.25, 0.75, 0.375, 0.75), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.625, 0.25, 0.75, 1, 0.75), BooleanOp.OR);
		
		return shape.optimize();
	}
	
	private static VoxelShape makeBottomShape() {
		VoxelShape shape = Shapes.empty();
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.125, 0.5, 0.75, 0.25, 1, 0.875), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.5, 0.75, 0.875, 1, 0.875), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.125, 0.5, 0.125, 0.25, 1, 0.25), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.5, 0.125, 0.875, 1, 0.25), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.5, 0.875), BooleanOp.OR);
		shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.5, 0.25, 0.75, 1, 0.75), BooleanOp.OR);
		
		return shape.optimize();
	}
}
