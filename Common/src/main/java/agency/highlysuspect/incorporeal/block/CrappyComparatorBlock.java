package agency.highlysuspect.incorporeal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.annotations.SoftImplement;

import java.util.List;
import java.util.Random;

/**
 * The "natural comparator". It's like a comparator, but has a 1-second delay, and only outputs 15 or 0.
 * Because it's "like a comparator", the bulk of this class is a straight copy-paste of the comparator class.
 * Funny how that works.
 * 
 * The SENSITIVE property controls how the mapping from analog comparator signals to the digital on/off signal is done.
 * If it is sensitive, every non-zero signal maps to "on", and only a signal of zero maps to "off".
 * If not, only a signal of 15 maps to "on", and everything else maps to "off".
 */
public class CrappyComparatorBlock extends CrappyDiodeBlock {
	public CrappyComparatorBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState()
			.setValue(SENSITIVE, false));
	}
	
	public static final BooleanProperty SENSITIVE = BooleanProperty.create("sensitive");
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
		super.createBlockStateDefinition($$0.add(SENSITIVE));
	}
	
	//from ComparatorBlock, modified to read blockstate instead of a BE
	@Override
	protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
		return state.getValue(POWERED) ? 15 : 0;
	}
	
	//Styled after ComparatorBlock calculateOutputSignal, but not a copypaste.
	//In 1.16 i didnt have the side input... but i think it doesn't hurt to have
	private boolean calculateOutput(Level level, BlockPos pos, BlockState state) {
		int back = getInputSignal(level, pos, state);
		if(back == 0) return false;
		
		int side = getAlternateSignal(level, pos, state);
		if(side > back) return false;
		
		int difference = back - side;
		if(state.getValue(SENSITIVE)) return difference != 0;
		else return difference == 15;
	}
	
	//copypaste
	@Override
	protected int getInputSignal(Level level, BlockPos blockPos, BlockState blockState) {
		int i = super.getInputSignal(level, blockPos, blockState);
		Direction direction = (Direction)blockState.getValue(FACING);
		BlockPos blockPos2 = blockPos.relative(direction);
		BlockState blockState2 = level.getBlockState(blockPos2);
		if (blockState2.hasAnalogOutputSignal()) {
			i = blockState2.getAnalogOutputSignal(level, blockPos2);
		} else if (i < 15 && blockState2.isRedstoneConductor(level, blockPos2)) {
			blockPos2 = blockPos2.relative(direction);
			blockState2 = level.getBlockState(blockPos2);
			ItemFrame itemFrame = this.getItemFrame(level, direction, blockPos2);
			int j = Math.max(itemFrame == null ? -2147483648 : itemFrame.getAnalogOutput(), blockState2.hasAnalogOutputSignal() ? blockState2.getAnalogOutputSignal(level, blockPos2) : -2147483648);
			if (j != -2147483648) {
				i = j;
			}
		}
		
		return i;
	}
	
	//copypaste
	@Nullable
	private ItemFrame getItemFrame(Level level, Direction direction, BlockPos blockPos) {
		List<ItemFrame> list = level.getEntitiesOfClass(ItemFrame.class, new AABB((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)(blockPos.getX() + 1), (double)(blockPos.getY() + 1), (double)(blockPos.getZ() + 1)), (itemFrame) -> {
			return itemFrame != null && itemFrame.getDirection() == direction;
		});
		return list.size() == 1 ? (ItemFrame)list.get(0) : null;
	}
	
	//copypaste
	protected int getAlternateSignal(LevelReader level, BlockPos pos, BlockState state) {
		Direction $$3 = (Direction)state.getValue(FACING);
		Direction $$4 = $$3.getClockWise();
		Direction $$5 = $$3.getCounterClockWise();
		return Math.max(this.getAlternateSignalAt(level, pos.relative($$4), $$4), this.getAlternateSignalAt(level, pos.relative($$5), $$5));
	}
	
	//copypaste
	protected int getAlternateSignalAt(LevelReader level, BlockPos pos, Direction direction) {
		BlockState $$3 = level.getBlockState(pos);
		if (this.isAlternateInput($$3)) {
			if ($$3.is(Blocks.REDSTONE_BLOCK)) {
				return 15;
			} else {
				return $$3.is(Blocks.REDSTONE_WIRE) ? (Integer)$$3.getValue(RedStoneWireBlock.POWER) : level.getDirectSignal(pos, direction);
			}
		} else {
			return 0;
		}
	}
	
	//copypaste
	@Override
	public boolean isSignalSource(BlockState $$0) {
		return true;
	}
	
	//Copypaste modified to change the SENSITIVE property
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		} else {
			blockState = (BlockState)blockState.cycle(SENSITIVE);
			float f = blockState.getValue(SENSITIVE) ? 0.55F : 0.5F;
			level.playSound(player, blockPos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, f);
			level.setBlock(blockPos, blockState, 2);
			this.refreshOutputState(level, blockPos, blockState);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}
	
	//Copypaste modified to use a bool
	//Mcp method named this "updateState" lol
	protected void checkTickOnNeighbor(Level level, BlockPos blockPos, BlockState blockState) {
		if (!level.getBlockTicks().willTickThisTick(blockPos, this)) {
			//int i = this.calculateOutputSignal(level, blockPos, blockState);
			//BlockEntity blockEntity = level.getBlockEntity(blockPos);
			//int j = blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
			boolean i = calculateOutput(level, blockPos, blockState);
			boolean j = blockState.getValue(POWERED);
			if (i != j /*|| (Boolean)blockState.getValue(POWERED) != this.shouldTurnOn(level, blockPos, blockState)*/) {
				TickPriority tickPriority = this.shouldPrioritize(level, blockPos, blockState) ? TickPriority.HIGH : TickPriority.NORMAL;
				//changed hardcoded 2 ticks to getDelay
				level.scheduleTick(blockPos, this, getDelay(blockState), tickPriority);
			}
			
		}
	}
	
	//not really a copypaste anymore lol
	private void refreshOutputState(Level level, BlockPos pos, BlockState state) {
		boolean shouldPower = calculateOutput(level, pos, state);
		boolean isPowered = state.getValue(POWERED);
		
		if(shouldPower != isPowered) {
			level.setBlock(pos, state.setValue(POWERED, shouldPower), 2);
			updateNeighborsInFront(level, pos, state);
		}
	}
	
	//copypaste
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		refreshOutputState(level, pos, state);
	}
	
	//Weird Forge extensions
	@SuppressWarnings("unused")
	@SoftImplement("IForgeBlock")
	public boolean getWeakChanges(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos) {
		//return state.is(Blocks.COMPARATOR);
		return true;
	}
	
	@SuppressWarnings("unused")
	@SoftImplement("IForgeBlock")
	public void onNeighborChange(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, BlockPos neighbor) {
		if (pos.getY() == neighbor.getY() && world instanceof Level && !((Level)world).isClientSide()) {
			state.neighborChanged((Level)world, pos, world.getBlockState(neighbor).getBlock(), neighbor, false);
		}
	}
}
