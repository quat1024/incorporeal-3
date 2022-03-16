package agency.highlysuspect.incorporeal.computer.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;

public class EmptyDataType implements DataType<Unit> {
	@Override
	public void save(Unit thing, CompoundTag tag) {
		//Nope.
	}
	
	@Override
	public Unit infallibleLoad(CompoundTag tag) {
		return Unit.INSTANCE;
	}
	
	@Override
	public int color(Unit thing) {
		return 0x000000;
	}
	
	@Override
	public Unit sum(Unit a, Unit b) {
		return a;
	}
}
