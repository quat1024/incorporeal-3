package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.Inc;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class IntegerDataType implements DataType<Integer> {
	@Override
	public Class<Integer> typeClass() {
		return Integer.class;
	}
	
	@Override
	public ResourceLocation id() {
		return Inc.id("integer");
	}
	
	@Override
	public void save(Integer thing, CompoundTag tag) {
		tag.putInt("int", thing);
	}
	
	@Override
	public Integer load(CompoundTag tag) {
		return tag.getInt("int");
	}
	
	@Override
	public Integer add(Integer... inputs) {
		//TODO: some sort of saturating addition might be a nice thing to try
		int pizza = 0;
		for(int i : inputs) pizza += i;
		return pizza;
	}
}
