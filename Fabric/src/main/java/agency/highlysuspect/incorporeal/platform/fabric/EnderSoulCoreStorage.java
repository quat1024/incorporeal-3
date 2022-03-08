package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import com.google.common.primitives.Ints;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public record EnderSoulCoreStorage(EnderSoulCoreBlockEntity be) implements Storage<ItemVariant> {
	public EnderSoulCoreStorage(EnderSoulCoreBlockEntity entity, Direction unused) {
		this(entity);
	}
	
	//TODO: maybe cache this for one tick, or something
	private Storage<ItemVariant> getDelegate() {
		PlayerEnderChestContainer enderChest = be.getEnderChest();
		if(enderChest == null) return Storage.empty();
		else if(be.isCompletelyUnmasked()) return InventoryStorage.of(enderChest, null);
		
		List<Storage<ItemVariant>> maskedList = new ArrayList<>();
		for(int i = 0; i < enderChest.getContainerSize(); i++) {
			if(be.canAccessSlot(i)) maskedList.add(new YourApiSucks(enderChest, i));
		}
		return new CombinedStorage<>(maskedList);
	}
	
	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long howMuch = getDelegate().insert(resource, maxAmount, transaction);
		
		transaction.addOuterCloseCallback(result -> {
			if(result.wasCommitted()) be.trackItemMovement(Ints.saturatedCast(howMuch));
		});
		
		return howMuch;
	}
	
	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long howMuch = getDelegate().extract(resource, maxAmount, transaction);
		
		transaction.addOuterCloseCallback(result -> {
			if(result.wasCommitted()) be.trackItemMovement(Ints.saturatedCast(howMuch));
		});
		
		return howMuch;
	}
	
	//feel free to yell at me for this
	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		Storage<ItemVariant> delegate = getDelegate();
		Iterator<StorageView<ItemVariant>> delegaterator = delegate.iterator(transaction);
		
		return new Iterator<>() {
			@Override
			public boolean hasNext() {
				return delegaterator.hasNext();
			}
			
			@Override
			public StorageView<ItemVariant> next() {
				StorageView<ItemVariant> slotDelegate = delegaterator.next();
				return new StorageView<>() {
					@Override
					public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
						//yes its the same as the one above
						long howMuch = slotDelegate.extract(resource, maxAmount, transaction);
						
						transaction.addOuterCloseCallback(result -> {
							if(result.wasCommitted()) be.trackItemMovement(Ints.saturatedCast(howMuch));
						});
						
						return howMuch;
					}
					
					@Override
					public boolean isResourceBlank() {
						return slotDelegate.isResourceBlank();
					}
					
					@Override
					public ItemVariant getResource() {
						return slotDelegate.getResource();
					}
					
					@Override
					public long getAmount() {
						return slotDelegate.getAmount();
					}
					
					@Override
					public long getCapacity() {
						return slotDelegate.getCapacity();
					}
				};
			}
		};
	}
	
	@Override
	public boolean supportsInsertion() {
		return getDelegate().supportsInsertion();
	}
	
	@Override
	public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		return getDelegate().simulateInsert(resource, maxAmount, transaction);
	}
	
	@Override
	public boolean supportsExtraction() {
		return getDelegate().supportsExtraction();
	}
	
	@Override
	public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		return getDelegate().simulateExtract(resource, maxAmount, transaction);
	}
	
	//Fucking reimplementation of InventorySlotWrapper because its private in fabric transfer api and bound too closely to InventoryStorageImpl
	public static class YourApiSucks extends SingleStackStorage {
		private final PlayerEnderChestContainer pissOff;
		final int slot;
		private ItemStack whoCares = null;
		
		public YourApiSucks(PlayerEnderChestContainer pissOff, int slot) {
			this.pissOff = pissOff;
			this.slot = slot;
		}
		
		@Override
		protected ItemStack getStack() {
			return pissOff.getItem(slot);
		}
		
		@Override
		protected void setStack(ItemStack stack) {
			pissOff.setItem(slot, stack);
		}
		
		@Override
		protected boolean canInsert(ItemVariant itemVariant) {
			return pissOff.canPlaceItem(slot, itemVariant.toStack());
		}
		
		@Override
		protected int getCapacity(ItemVariant itemVariant) {
			return Math.min(pissOff.getMaxStackSize(), itemVariant.getItem().getMaxStackSize());
		}
		
		@Override
		public void updateSnapshots(TransactionContext transaction) {
			//The real one tries to be more clever about this but i dont give a shit
			//Fuck off
			pissOff.setChanged();
			super.updateSnapshots(transaction);
		}
		
		@Override
		protected void releaseSnapshot(ItemStack snapshot) {
			whoCares = snapshot;
		}
		
		//coy pasting
		@Override
		protected void onFinalCommit() {
			// Try to apply the change to the original stack
			ItemStack original = whoCares;
			ItemStack currentStack = getStack();
			
			if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
				// None is empty and the items match: just update the amount and NBT, and reuse the original stack.
				original.setCount(currentStack.getCount());
				original.setTag(currentStack.hasTag() ? currentStack.getTag().copy() : null);
				setStack(original);
			} else {
				// Otherwise assume everything was taken from original so empty it.
				original.setCount(0);
			}
		}
	}
}
