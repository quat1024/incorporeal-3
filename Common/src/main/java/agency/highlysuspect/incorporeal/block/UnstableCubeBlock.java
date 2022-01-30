package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.entity.UnstableCubeBlockEntity;
import agency.highlysuspect.incorporeal.client.UnstableCubeClientTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.BlockModWaterloggable;

public class UnstableCubeBlock extends BlockModWaterloggable implements EntityBlock {
	public UnstableCubeBlock(DyeColor color, Properties $$0) {
		super($$0);
		this.color = color;
	}
	
	public final DyeColor color;
	public static final VoxelShape SHAPE = Shapes.create(3/16d, 3/16d, 3/16d, 1 - 3/16d, 1 - 3/16d, 1 - 3/16d);
	
	@Override
	public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
		return SHAPE;
	}
	
	private void punch(Level level, BlockPos pos, @Nullable Player puncher) {
		if(level.getBlockEntity(pos) instanceof UnstableCubeBlockEntity be) be.punch(puncher);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		punch(level, pos, player);
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public void attack(BlockState state, Level level, BlockPos pos, Player player) {
		punch(level, pos, player);
	}
	
	@Override
	public boolean isSignalSource(BlockState $$0) {
		return true;
	}
	
	@Override
	public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
		return getDirectSignal($$0, $$1, $$2, $$3);
	}
	
	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		if(level.getBlockEntity(pos) instanceof UnstableCubeBlockEntity be) return be.getPower();
		else return 0;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState $$0) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IncBlockEntityTypes.UNSTABLE_CUBE.create(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, IncBlockEntityTypes.UNSTABLE_CUBE,
			level.isClientSide() ? UnstableCubeClientTicker::clientTick : UnstableCubeBlockEntity::serverTick);
	}
}
