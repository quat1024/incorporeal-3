package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.computer.types.DataType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

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
	public int magicNumber() {
		return 0;
	}
	
	@Override
	public int color(Unit thing) {
		return 0x111111;
	}
	
	@Override
	public int signal(Unit thing) {
		return 0;
	}
	
	@Override
	public Component describe(Unit thing) {
		return TextComponent.EMPTY;
	}
	
	@Override
	public Unit parse(String message, ItemStack otherHand) {
		return Unit.INSTANCE;
	}
}
