package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.corporea.RequestParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * A DataType representing whole numbers.
 */
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
	public int magicNumber() {
		return 1;
	}
	
	@Override
	public int color(Integer thing) {
		return 0xFF2222;
	}
	
	@Override
	public int signal(Integer thing) {
		return Mth.clamp(thing, 0, 15);
	}
	
	@Override
	public Component describe(Integer thing) {
		return new TextComponent(Integer.toString(thing));
	}
	
	@Override
	public Integer parse(String message, ItemStack otherHand) {
		return RequestParser.parseCount(message, otherHand);
	}
}
