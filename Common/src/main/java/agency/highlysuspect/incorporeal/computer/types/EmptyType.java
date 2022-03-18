package agency.highlysuspect.incorporeal.computer.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;

/**
 * A DataType representing... nothing. The type of the empty Datum.
 */
public class EmptyType implements DataType<Unit> {
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
		return 0x111111;
	}
	
	@Override
	public int signal(Unit thing) {
		return 0;
	}
}
