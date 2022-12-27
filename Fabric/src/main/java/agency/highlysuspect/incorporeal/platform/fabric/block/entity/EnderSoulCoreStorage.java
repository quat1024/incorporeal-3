package agency.highlysuspect.incorporeal.platform.fabric.block.entity;

import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import com.google.common.primitives.Ints;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public record EnderSoulCoreStorage(EnderSoulCoreBlockEntity be) implements Storage<ItemVariant> {
	public EnderSoulCoreStorage(EnderSoulCoreBlockEntity entity, Direction unused) {
		this(entity);
	}
	
	private Storage<ItemVariant> getDelegate() {
		PlayerEnderChestContainer enderChest = be.getEnderChest();
		if(enderChest == null) return Storage.empty();
		else return InventoryStorage.of(enderChest, null);
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
	public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
		Storage<ItemVariant> delegate = getDelegate();
		Iterator<? extends StorageView<ItemVariant>> delegaterator = delegate.iterator();
		
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
}
