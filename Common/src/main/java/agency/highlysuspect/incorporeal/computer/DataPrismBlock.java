package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class DataPrismBlock extends Block implements EntityBlock {
	public DataPrismBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState()
			.setValue(BlockStateProperties.FACING, Direction.UP)
			.setValue(BlockStateProperties.POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
		super.createBlockStateDefinition($$0.add(BlockStateProperties.FACING, BlockStateProperties.POWERED));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean moving) {
		boolean shouldPower = level.hasNeighborSignal(pos);
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			
			if(!level.isClientSide() && level.getBlockEntity(pos) instanceof DataPrismBlockEntity prism) {
				prism.act(pos, state.getValue(BlockStateProperties.FACING));
			}
		}
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IncBlockEntityTypes.DATA_PRISM.create(pos, state);
	}
}
