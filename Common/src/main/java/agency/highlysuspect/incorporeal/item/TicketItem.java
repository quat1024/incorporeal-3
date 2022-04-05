package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * An item that may carry a Datum<?>.
 * 
 * @see TicketConjurerItem for a producer
 * @see agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock for another producer
 * @see agency.highlysuspect.incorporeal.block.entity.SanvocaliaBlockEntity for a consumer
 */
public class TicketItem<T> extends Item {
	public TicketItem(DataType<T> type, Properties properties) {
		super(properties);
		this.type = type;
	}
	
	public final DataType<T> type;
	
	public ItemStack produce(T thing) {
		ItemStack stack = new ItemStack(this);
		
		CompoundTag tag = stack.getOrCreateTagElement("datum");
		type.save(thing, tag);
		
		return stack;
	}
	
	public Datum<?> get(ItemStack stack) {
		if(!stack.hasTag()) return Datum.EMPTY;
		
		CompoundTag tag = stack.getTagElement("datum");
		if(tag == null) return Datum.EMPTY;
		
		return type.datumOf(type.tryLoad(tag).orElse(type.defaultValue()));
	}
	
	@Override
	public Component getName(ItemStack stack) {
		return new TranslatableComponent(this.getDescriptionId(stack), get(stack).describe());
	}
}
