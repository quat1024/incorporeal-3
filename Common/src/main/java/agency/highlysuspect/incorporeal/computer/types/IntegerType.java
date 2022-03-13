package agency.highlysuspect.incorporeal.computer.types;

import net.minecraft.nbt.CompoundTag;

public class IntegerType implements DataType<Integer> {
	@Override
	public void save(Integer thing, CompoundTag tag) {
		tag.putInt("int", thing);
	}
	
	@Override
	public Integer infallibleLoad(CompoundTag tag) {
		return tag.getInt("int");
	}
	
	@Override
	public Integer sum(Integer... inputs) {
		//TODO: some sort of saturating addition might be a nice thing to try
		int pizza = 0;
		for(int i : inputs) pizza += i;
		return pizza;
	}
}
