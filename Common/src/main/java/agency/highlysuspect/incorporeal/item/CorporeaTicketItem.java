package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * An item that may carry a SolidifiedRequest.
 * 
 * @see TicketConjurerItem for a producer
 * @see agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock for another producer
 * @see agency.highlysuspect.incorporeal.block.entity.SanvocaliaBlockEntity for a consumer
 */
public class CorporeaTicketItem extends Item {
	public CorporeaTicketItem(Properties properties) {
		super(properties);
	}
	
	private static final String KEY = "solidified_corporea_request";
	
	public ItemStack produce(SolidifiedRequest request) {
		ItemStack stack = new ItemStack(this);
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(KEY, request.save());
		return stack;
	}
	
	public Optional<SolidifiedRequest> tryGetRequest(ItemStack stack) {
		if(!stack.hasTag() || stack.getTagElement(KEY) == null) return Optional.empty();
		else return SolidifiedRequest.tryLoad(stack.getTagElement(KEY));
	}
	
	public boolean hasRequest(ItemStack stack) {
		return tryGetRequest(stack).isPresent();
	}
	
	@Override
	public Component getName(ItemStack stack) {
		return tryGetRequest(stack)
			.<Component>map(request -> new TranslatableComponent("item.incorporeal.corporea_ticket.has", request.toComponent()))
			.orElseGet(() -> super.getName(stack));
	}
}
