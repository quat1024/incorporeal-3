package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.xplat.IXplatAbstractions;

import javax.annotation.Nonnull;

/**
 * Implementatoin of the Red Stringed Constrictor on Forge.
 * Here it uses the platform's IItemHandler abstraction and capabilities.
 */
public class ForgeRedStringConstrictorBlockEntity extends RedStringConstrictorBlockEntity {
	public ForgeRedStringConstrictorBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
	
	@Override
	public boolean acceptBlock(BlockPos pos) {
		assert level != null;
		
		BlockEntity be = level.getBlockEntity(pos);
		return be != null && IXplatAbstractions.INSTANCE.isRedStringContainerTarget(be); //out of laziness
	}
	
	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			BlockEntity binding = getTileAtBinding();
			if(binding == null) return LazyOptional.of(() -> EmptyHandler.INSTANCE).cast();
			else return binding.getCapability(cap, side)
				.<IItemHandler>cast()
				.lazyMap(handler -> sliceStorage((start, end) -> makeRangedWrapper(handler, start, end), handler.getSlots(), () -> EmptyHandler.INSTANCE))
				.cast();
		}
		
		return LazyOptional.empty();
	}
	
	/**
	 * This is a slightly modified copy-paste of a class from Forge.
	 * It was probably not meant for external use by mods; it's used when wrapping vanilla containers into IItemHandler.
	 * The reason I pasted the class instead of just using it, is because in Forge, everyting extends IItemHandlerModifiable instead of IItemHandler.
	 * That is not important for my use case.
	 */
	public static class NotIItemHandlerModifiableRangedWrapper implements IItemHandler {
		private final IItemHandler compose;
		private final int minSlot;
		private final int maxSlot;
		
		public NotIItemHandlerModifiableRangedWrapper(IItemHandler compose, int minSlot, int maxSlotExclusive) {
			Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
			this.compose = compose;
			this.minSlot = minSlot;
			this.maxSlot = maxSlotExclusive;
		}
		
		@Override
		public int getSlots() {
			return maxSlot - minSlot;
		}
		
		@Override
		@Nonnull
		public ItemStack getStackInSlot(int slot) {
			if(checkSlot(slot)) {
				return compose.getStackInSlot(slot + minSlot);
			}
			
			return ItemStack.EMPTY;
		}
		
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if(checkSlot(slot)) {
				return compose.insertItem(slot + minSlot, stack, simulate);
			}
			
			return stack;
		}
		
		@Override
		@Nonnull
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if(checkSlot(slot)) {
				return compose.extractItem(slot + minSlot, amount, simulate);
			}
			
			return ItemStack.EMPTY;
		}
		
		/*
		@Override
		public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
			if(checkSlot(slot)) {
				compose.setStackInSlot(slot + minSlot, stack);
			}
		}
		*/
		
		@Override
		public int getSlotLimit(int slot) {
			if(checkSlot(slot)) {
				return compose.getSlotLimit(slot + minSlot);
			}
			
			return 0;
		}
		
		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			if(checkSlot(slot)) {
				return compose.isItemValid(slot + minSlot, stack);
			}
			
			return false;
		}
		
		private boolean checkSlot(int localSlot) {
			return localSlot + minSlot < maxSlot;
		}
	}
	
	/**
	 * That said, there's no harm in forwarding the IItemHandlerModifiable-ness along, if the handler really is modifiable.
	 */
	public static IItemHandler makeRangedWrapper(IItemHandler handler, int start, int end) {
		if(handler instanceof IItemHandlerModifiable modifiable) return new RangedWrapper(modifiable, start, end);
		else return new NotIItemHandlerModifiableRangedWrapper(handler, start, end);
	}
}
