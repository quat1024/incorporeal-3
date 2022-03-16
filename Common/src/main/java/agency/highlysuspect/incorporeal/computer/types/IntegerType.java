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
	public int color(Integer thing) {
		return 0xFF2222;
	}
	
	@Override
	public Integer sum(Integer i, Integer j) {
		return i + j;
	}
}
