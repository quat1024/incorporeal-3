package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DataFunnelBlock extends Block implements EntityBlock {
	public DataFunnelBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState()
			.setValue(BlockStateProperties.POWERED, false));
	}
	
	//Same as mana spreader
	private static final VoxelShape SHAPE = box(2, 2, 2, 14, 14, 14);
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
		super.createBlockStateDefinition($$0.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean moving) {
		boolean shouldPower = level.hasNeighborSignal(pos);
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			
			if(shouldPower && !level.isClientSide() && level.getBlockEntity(pos) instanceof DataFunnelBlockEntity funnel) {
				funnel.doIt();
			}
		}
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IncBlockEntityTypes.DATA_FUNNEL.create(pos, state);
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if(level.getBlockEntity(pos) instanceof DataStorageBlockEntity storage) return storage.signal();
		else return 0;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState $$0) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
}
