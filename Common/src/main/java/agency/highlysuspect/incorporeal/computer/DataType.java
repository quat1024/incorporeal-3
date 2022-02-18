package agency.highlysuspect.incorporeal.computer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface DataType<T> {
	Class<T> typeClass();
	ResourceLocation id();
	
	void save(T thing, CompoundTag tag);
	T load(CompoundTag tag);
	
	//Go directly to Java generice hell. Do not pass Go, do not collect $200
	@SuppressWarnings("unchecked")
	default void erasedSave(Object thing, CompoundTag tag) {
		save((T) thing, tag);
	}
	
	/**
	 * Adds the inputs together
	 */
	@SuppressWarnings("unchecked") //Can't do SafeVarargs so play nice, please
	T add(T... inputs);
}
