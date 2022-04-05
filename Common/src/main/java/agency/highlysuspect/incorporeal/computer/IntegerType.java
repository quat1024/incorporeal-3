package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.corporea.RequestParser;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import agency.highlysuspect.incorporeal.item.TicketItem;
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
	public Integer defaultValue() {
		return 0;
	}
	
	@Override
	public TicketItem<Integer> ticketItem() {
		return IncItems.INTEGER_TICKET;
	}
	
	@Override
	public TicketConjurerItem<Integer> conjurerItem() {
		return IncItems.INTEGER_CONJURER;
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
