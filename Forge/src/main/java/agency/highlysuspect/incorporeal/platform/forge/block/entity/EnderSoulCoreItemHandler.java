package agency.highlysuspect.incorporeal.platform.forge.block.entity;

import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public record EnderSoulCoreItemHandler(EnderSoulCoreBlockEntity be) implements IItemHandler {
	public IItemHandler getDelegate() {
		PlayerEnderChestContainer enderChest = be.getEnderChest();
		if(enderChest == null) return EmptyHandler.INSTANCE;
		else return new InvWrapper(enderChest);
	}
	
	@Override
	public int getSlots() {
		return getDelegate().getSlots();
	}
	
	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return getDelegate().getStackInSlot(slot);
	}
	
	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ItemStack leftover = getDelegate().insertItem(slot, stack, simulate);
		if(!simulate) be.trackItemMovement(stack.getCount() - leftover.getCount());
		return leftover;
	}
	
	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack extracted = getDelegate().extractItem(slot, amount, simulate);
		if(!simulate) be.trackItemMovement(extracted.getCount());
		return extracted;
	}
	
	@Override
	public int getSlotLimit(int slot) {
		return getDelegate().getSlotLimit(slot);
	}
	
	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return getDelegate().isItemValid(slot, stack);
	}
}
