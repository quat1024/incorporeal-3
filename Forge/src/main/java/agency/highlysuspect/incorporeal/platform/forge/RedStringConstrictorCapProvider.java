package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public record RedStringConstrictorCapProvider(RedStringConstrictorBlockEntity be) implements ICapabilityProvider {
	private static final LazyOptional<IItemHandler> EMPTY = LazyOptional.of(EmptyHandler::new);
	
	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			BlockEntity binding = be.getTileAtBinding();
			if(binding != null) {
				LazyOptional<?> optional = binding.getCapability(cap, side);
				if(optional.isPresent()) {
					@SuppressWarnings("OptionalGetWithoutIsPresent")
					IItemHandler handler = optional.<IItemHandler>cast().resolve().get();
					
					int start, end;
					if(be.removesSlotsFromFront()) {
						start = be.removedSlotCount();
						end = handler.getSlots();
					} else {
						start = 0;
						end = handler.getSlots() - be.removedSlotCount();
					}
					
					if(end <= start) return EMPTY.cast();
					else return LazyOptional.<IItemHandler>of(() -> new RangedWrapper(makeModifiable(handler), start, end)).cast();
				} else {
					return EMPTY.cast();
				}
			}
		}
		
		return LazyOptional.empty();
	}
	
	//Forge's RangedWrapper only accepts IItemHandlerModifiable instances. This appears to be a mistake?
	public static IItemHandlerModifiable makeModifiable(IItemHandler handler) {
		if(handler instanceof IItemHandlerModifiable handlerM) return handlerM;
		else return new NotActuallyModifiable(handler);
	}
	
	public static record NotActuallyModifiable(IItemHandler handler) implements IItemHandlerModifiable {
		@Override
		public void setStackInSlot(int slot, @NotNull ItemStack stack) {
			//Nope
		}
		
		@Override
		public int getSlots() {return handler.getSlots();}
		
		@Override
		@Nonnull
		public ItemStack getStackInSlot(int slot) {return handler.getStackInSlot(slot);}
		
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {return handler.insertItem(slot, stack, simulate);}
		
		@Override
		@Nonnull
		public ItemStack extractItem(int slot, int amount, boolean simulate) {return handler.extractItem(slot, amount, simulate);}
		
		@Override
		public int getSlotLimit(int slot) {return handler.getSlotLimit(slot);}
		
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {return handler.isItemValid(slot, stack);}
	}
}
