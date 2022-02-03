package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

/**
 * A block that decrements the request size in nearby Corporea Retainers. Kind of a holdover from
 * before I came up with the whole cygnus idea, because you can do the same thing with that.
 * 
 * Hey, did you know Corporea Interceptors emit their request to *all* adjacent Retainers, so even
 * if you evaporate one until it's empty, you can still have an untouched request in a different
 * Retainer? Now you do.
 */
public class CorporeaRetainerEvaporatorBlock extends Block {
	public CorporeaRetainerEvaporatorBlock(Properties props) {
		super(props);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		updatePoweredState(level, pos, state);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean moved) {
		updatePoweredState(level, pos, state);
	}
	
	private void updatePoweredState(Level level, BlockPos pos, BlockState state) {
		boolean shouldPower = level.hasNeighborSignal(pos);
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		
		if(shouldPower != isPowered) {
			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			if(shouldPower) {
				for(Direction horiz : Direction.Plane.HORIZONTAL) {
					BlockEntity be = level.getBlockEntity(pos.relative(horiz));
					if(be instanceof RetainerDuck duck) {
						//TODO: maybe use capability again, like in 1.16. But it will need to be cross-loader now.
						duck.inc$setCount(Math.max(0, duck.inc$getCount() - 1));
					}
				}
			}
		}
	}
}
