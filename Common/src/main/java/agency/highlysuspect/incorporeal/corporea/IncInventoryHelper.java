package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * More utilities for working with inventories, named after Botania's InventoryHelper.
 * 
 * The name is a bit of an anachronism, because currently Botania works in terms of Containers. :)
 */
public class IncInventoryHelper {
	public static @Nullable Container getSidedContainerAt(Level level, BlockPos pos, Direction direction) {
		//Copy from VanillaNodeDetector from Botania, which itself copies a bit from hoppers
		Container container = null;
		BlockState blockState = level.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof WorldlyContainerHolder worldlyContainer) {
			container = worldlyContainer.getContainer(blockState, level, pos);
		} else if (blockState.hasBlockEntity()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof Container beContainer) {
				container = beContainer;
				if (container instanceof ChestBlockEntity && block instanceof ChestBlock chest) {
					container = ChestBlock.getContainer(chest, blockState, level, pos, true);
				}
			}
		}
		
		//sigh
		if (container instanceof WorldlyContainer worldlyContainer) return new WorldlyContainerView(worldlyContainer, direction);
		else return container;
	}
	
	public static int countItems(Container container) {
		int total = 0;
		for(int i = 0; i < container.getContainerSize(); i++) {
			total += container.getItem(i).getCount();
		}
		return total;
	}
	
	/**
	 * Views a WorldlyContainer through a particular direction.
	 */
	public static class WorldlyContainerView implements Container {
		public WorldlyContainerView(WorldlyContainer wc, Direction direction) {
			this.wc = wc;
			this.slotMapping = wc.getSlotsForFace(direction);
		}
		
		private final WorldlyContainer wc;
		private final int[] slotMapping;
		
		@Override
		public int getContainerSize() {
			return slotMapping.length;
		}
		
		@Override
		public int getMaxStackSize() {
			return wc.getMaxStackSize();
		}
		
		@Override
		public boolean isEmpty() {
			for(int slot : slotMapping) {
				if(!wc.getItem(slot).isEmpty()) return false;
			}
			return true;
		}
		
		@Override
		public ItemStack getItem(int slot) {
			return wc.getItem(slotMapping[slot]);
		}
		
		@Override
		public ItemStack removeItem(int slot, int howMuch) {
			return wc.removeItem(slotMapping[slot], howMuch);
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int slot) {
			return wc.removeItemNoUpdate(slotMapping[slot]);
		}
		
		@Override
		public void setItem(int slot, ItemStack stack) {
			wc.setItem(slotMapping[slot], stack);
		}
		
		@Override
		public void startOpen(Player player) {
			wc.startOpen(player);
		}
		
		@Override
		public void stopOpen(Player player) {
			wc.stopOpen(player);
		}
		
		@Override
		public boolean canPlaceItem(int slot, ItemStack stack) {
			return wc.canPlaceItem(slotMapping[slot], stack);
		}
		
		@Override
		public void setChanged() {
			wc.setChanged();
		}
		
		@Override
		public boolean stillValid(Player player) {
			return wc.stillValid(player);
		}
		
		@Override
		public void clearContent() {
			wc.clearContent();
		}
	}
	
	public static void giveToPlayer(ItemStack stack, ServerPlayer player) {
		if (!player.getInventory().add(stack)) {
			player.drop(stack, false);
		}
	}
}
