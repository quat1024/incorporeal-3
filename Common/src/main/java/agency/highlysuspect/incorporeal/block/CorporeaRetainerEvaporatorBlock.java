package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncSounds;
import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
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
		
		registerDefaultState(defaultBlockState()
			.setValue(BlockStateProperties.POWERED, false)
			.setValue(SETTING, CorporeaFrameRotation.ONE));
	}
	
	public static final EnumProperty<CorporeaFrameRotation> SETTING = EnumProperty.create("setting", CorporeaFrameRotation.class);
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED, SETTING));
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		state = state.cycle(SETTING);
		
		level.setBlockAndUpdate(pos, state);
		level.playSound(player, pos, IncSounds.RETAINER_EVAPORATOR_CLICK, SoundSource.BLOCKS, 1f,
			Inc.rangeRemap(state.getValue(SETTING).ordinal(), 0, 7, 0.3f, 2f));
		
		return InteractionResult.SUCCESS;
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
						duck.inc$setCount(Math.max(0, duck.inc$getCount() - state.getValue(SETTING).stackSize));
					}
				}
			}
		}
	}
	
	/**
	 * Enum-codified version of that corporea funnel mechanic where the rotation of an item frame corresponds to a number from 1 to 64.
	 */
	public enum CorporeaFrameRotation implements StringRepresentable {
		ONE(1), TWO(2), FOUR(4), EIGHT(8), SIXTEEN(16), THIRTY_TWO(32), FORTY_EIGHT(48), SIXTY_FOUR(64);
		
		CorporeaFrameRotation(int stackSize) {
			this.stackSize = stackSize;
		}
		public final int stackSize;
		
		@Override
		public String getSerializedName() {
			return Integer.toString(stackSize);
		}
	}
}
