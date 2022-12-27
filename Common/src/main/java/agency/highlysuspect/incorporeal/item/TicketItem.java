package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.computer.capabilities.DatumAcceptor;
import agency.highlysuspect.incorporeal.computer.capabilities.NotCapabilities;
import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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
		String key = getDescriptionId(stack);
		
		Datum<?> datum = get(stack);
		if(datum.isEmpty()) key += ".blank";
		
		return Component.translatable(key, datum.describe());
	}
	
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		@Nullable DatumAcceptor acceptor = NotCapabilities.findDatumAcceptor(ctx.getLevel(), ctx.getClickedPos(), null, null, null, true);
		if(acceptor != null) {
			acceptor.acceptDatum(get(ctx.getItemInHand()));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
