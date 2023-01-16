package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import vazkii.botania.common.block.block_entity.red_string.RedStringBlockEntity;

import java.util.function.Supplier;

/**
 * The Block Entity for the Red String Constrictor.
 * Abstract, because a bespoke implementation is created for each loader.
 */
public abstract class RedStringConstrictorBlockEntity extends RedStringBlockEntity {
	public RedStringConstrictorBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.RED_STRING_CONSTRICTOR, pos, state);
	}
	
	//Forwarding from TileRedString 
	@Override
	public abstract boolean acceptBlock(BlockPos pos);
	
	public boolean removesSlotsFromFront() {
		return !getBlockState().getValue(BlockStateProperties.INVERTED);
	}
	
	public int removedSlotCount() {
		return getBlockState().getValue(BlockStateProperties.POWER);
	}
	
	//TODO: allow binding these to each other (replace commontick with my own?)
	
	/**
	 * @param <T> - Whatever the storage abstraction is in this api.
	 * @param slicer - A function that takes (start inclusive, end exclusive) and returns a view over those slots of the storage.
	 * @param slotCount - How many slots are in that storage.
	 * @param empty - Provider for an empty storage, in case the Constrictor slices it to 0 slots.
	 */
	public <T> T sliceStorage(StorageSlicer<T> slicer, int slotCount, Supplier<T> empty) {
		int start, end;
		if(removesSlotsFromFront()) {
			start = removedSlotCount();
			end = slotCount;
		} else {
			start = 0;
			end = slotCount - removedSlotCount();
		}
		if(end <= start) return empty.get();
		else return slicer.slice(start, end);
	}
	
	public interface StorageSlicer<T> {
		T slice(int startInclusive, int endExclusive);
	}
}
