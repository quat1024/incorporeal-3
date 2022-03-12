package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import javax.annotation.Nullable;

/**
 * Implementation of the Red Stringed Constrictor on Fabric.
 * Here it uses fabric-transfer-api, but only if the inventory is slotted (CombinedStorage).
 */
@SuppressWarnings("UnstableApiUsage")
public class FabricRedStringConstrictorBlockEntity extends RedStringConstrictorBlockEntity {
	public FabricRedStringConstrictorBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
	
	//PASTE FROM BOTANIA RED STRING CONTAINER
	private BlockPos clientPos;
	
	@Override
	public void onBound(BlockPos pos) {
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		// We cannot query for the storage api on the client - so we send the binding position.
		BlockPos binding = getBinding();
		if (binding == null) {
			// hack: empty NBT gets the packet ignored but we don't want that
			cmp.putByte("-", (byte) 0);
			return;
		}
		cmp.putInt("bindX", binding.getX());
		cmp.putInt("bindY", binding.getY());
		cmp.putInt("bindZ", binding.getZ());
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		if (cmp.contains("bindX")) {
			clientPos = new BlockPos(cmp.getInt("bindX"), cmp.getInt("bindY"), cmp.getInt("bindZ"));
		} else {
			clientPos = null;
		}
	}
	
	@Nullable
	@Override
	public BlockPos getBinding() {
		assert level != null;
		return level.isClientSide ? clientPos : super.getBinding();
	}
	//END PASTE
	
	//I chose to impl this with an abstract method instead of xplat:
	@Override
	public boolean acceptBlock(BlockPos pos) {
		assert level != null;
		
		BlockEntity be = level.getBlockEntity(pos);
		if(be == null || be.getLevel() == null || be.getLevel().isClientSide()) return false;
		for(Direction d : Direction.values()) {
			Storage<ItemVariant> storage = ItemStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, d);
			if(acceptsStorage(storage)) return true;
		}
		
		return false;
	}
	
	public boolean acceptsStorage(Storage<ItemVariant> storage) {
		return storage instanceof CombinedStorage;
	}
	
	//The BlockEntityType is for RedStringConstrictorBlockEntity, no "fabric", so someone needs to downcast
	public static Storage<ItemVariant> getStorageWithDowncast(RedStringConstrictorBlockEntity be, Direction d) {
		return ((FabricRedStringConstrictorBlockEntity) be).getStorage(d);
	}
	
	public Storage<ItemVariant> getStorage(Direction d) {
		//Todo whatever idk, block api cache maybe
		// brain scrampled egg
		BlockEntity target = getTileAtBinding();
		if(target == null) return Storage.empty();
		
		Storage<ItemVariant> targetStorage = ItemStorage.SIDED.find(target.getLevel(), target.getBlockPos(), target.getBlockState(), target, d);
		if(!acceptsStorage(targetStorage)) return Storage.empty();
		assert targetStorage != null; //I seem to have broken IntelliJ's ability to inter nullability
		
		//justifying this cast:
		//CombinedStorage - see acceptsStorage
		//ItemVariant - ItemStorage was checked, not any other storage
		//? extends Storage<ItemVariant> - most relaxed constraint the generics on CombinedStorage will allow
		CombinedStorage<ItemVariant, ? extends Storage<ItemVariant>> combined = (CombinedStorage<ItemVariant, ? extends Storage<ItemVariant>>) targetStorage;
		
		return sliceStorage((start, end) -> new CombinedStorage<>(combined.parts.subList(start, end)), combined.parts.size(), Storage::empty);
	}
}
