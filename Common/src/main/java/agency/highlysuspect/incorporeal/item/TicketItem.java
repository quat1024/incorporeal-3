package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.computer.types.DataTypes;
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
public class TicketItem extends Item {
	public TicketItem(Properties properties) {
		super(properties);
	}
	
	private static final String KEY = "datum";
	
	public ItemStack produce(Datum<?> datum) {
		ItemStack stack = new ItemStack(this);
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(KEY, datum.save());
		return stack;
	}
	
	public Datum<?> get(ItemStack stack) {
		if(!stack.hasTag()) return Datum.EMPTY;
		
		CompoundTag tag = stack.getTagElement(KEY);
		if(tag == null) return Datum.EMPTY;
		else return Datum.load(tag);
	}
	
	@Override
	public Component getName(ItemStack stack) {
		//Oops it's a mess I'm sorry
		Datum<?> datum = get(stack);
		String baseName = this.getDescriptionId(stack) + '.' + DataTypes.REGISTRY.getKey(datum.type()).toString();
		return new TranslatableComponent(baseName, datum.describe());
	}
}
