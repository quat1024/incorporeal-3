package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.FrameReader;
import agency.highlysuspect.incorporeal.mixin.ItemFrameAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.botania.common.block.BlockModWaterloggable;
import vazkii.botania.mixin.AccessorItemEntity;

import java.util.List;

/**
 * A block that swaps any (single) items dropped on itself, with the items in nearby Item Frames.
 */
public class FrameTinkererBlock extends BlockModWaterloggable {
	public FrameTinkererBlock(Properties properties) {
		super(properties);
		
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}
	
	private static final double HEIGHT = 3 / 16d;
	private static final VoxelShape BOX = Shapes.box(0, 0, 0, 1, HEIGHT, 1);
	
	@Override
	public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
		return BOX;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.POWERED));
	}
	
	//Depositing an item onto the frame tinkerer.
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		if(!held.isEmpty()) {
			if(!level.isClientSide()) {
				ItemStack deposit;
				if(player.isCreative()) {
					deposit = held.copy();
					deposit.setCount(1);
				} else deposit = held.split(1);
				
				spawnStack(level, pos, deposit);
			}
			
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	//Powering the frame tinkerer.
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean moving) {
		boolean shouldPower = level.hasNeighborSignal(pos);
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		if(shouldPower != isPowered) {
			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldPower));
			
			if(!level.isClientSide() && shouldPower) {
				//Choose a random item entity resting on the frame tinkerer.
				List<ItemEntity> itemChoices = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos),
					//sanity checks to hopefully avoid dupes and stuff lol
					ent -> ent.isAlive() && !ent.getItem().isEmpty() &&
						//restrict to item entities with 1 item
						ent.getItem().getCount() == 1);
				
				//Choose a random item frame.
				//When switching an item entity with an item frame, it's okay if the frame is empty (it "places the item in the frame").
				//When I'm only taking an item out of an item frame, the frame has to be non-empty (because if not, i'd switch two empty frames with each other)
				boolean allowEmpty = !itemChoices.isEmpty();
				List<ItemFrame> frameChoices = allowEmpty ? FrameReader.near(level, pos) : FrameReader.nonEmptyNear(level, pos);
				
				if(frameChoices.isEmpty()) return; //No candidate frames to switch with.
				ItemFrame frame = Inc.choose(frameChoices, level.getRandom());
				
				if(itemChoices.isEmpty()) {
					switchWithNothing(level, pos, frame);
				} else {
					ItemEntity item = Inc.choose(itemChoices, level.getRandom());
					switchWithEntity(level, pos, frame, item);
				}
			}
		}
	}
	
	private static ItemStack removeStackFromFrame(ItemFrame frame) {
		//Prepare the ItemStack for removal from the frame (removes map markers, etc)
		((ItemFrameAccessor) frame).inc$removeFramedMap(frame.getItem());
		
		ItemStack frameItem = frame.getItem().copy();
		frame.setItem(ItemStack.EMPTY);
		return frameItem;
	}
	
	private static void switchWithNothing(Level level, BlockPos pos, ItemFrame frame) {
		//Remove the item from the frame
		ItemStack frameItem = removeStackFromFrame(frame);
		
		//ItemFrame#setItem doesn't play a sound when setting a frame to empty, so play the sound
		frame.playSound(frame.getRemoveItemSound(), 1f, 1f);
		
		//Spawn the item on the plate
		spawnStack(level, pos, frameItem);
	}
	
	private static void switchWithEntity(Level level, BlockPos pos, ItemFrame frame, ItemEntity switchWith) {
		//Remove the item from the frame
		ItemStack formerlyInFrame = removeStackFromFrame(frame);
		
		//Copy the item entity's item into the frame
		frame.setItem(switchWith.getItem());
		
		//Copy the frame's former item into the item entity
		switchWith.setItem(formerlyInFrame);
		switchWith.setPickUpDelay(30);
		((AccessorItemEntity) switchWith).setAge(0); //botania accessor
	}
	
	private static void spawnStack(Level level, BlockPos pos, ItemStack stack) {
		spawnStack(level, pos.getX() + .5, pos.getY() + HEIGHT, pos.getZ() + .5, stack);
	}
	
	private static void spawnStack(Level level, double x, double y, double z, ItemStack stack) {
		//Hey you can use the constructor that also sets velocity to 0 now! neat
		//It used to be sided lmao
		ItemEntity ent = new ItemEntity(level, x, y, z, stack, 0, 0, 0);
		ent.setPickUpDelay(30);
		level.addFreshEntity(ent);
	}
}
