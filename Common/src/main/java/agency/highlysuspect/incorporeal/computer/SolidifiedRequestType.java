package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.corporea.RequestParser;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import agency.highlysuspect.incorporeal.item.TicketItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * A DataType representing "solidified requests", which are a corporea request matcher + count pair.
 */
public class SolidifiedRequestType implements DataType<SolidifiedRequest> {
	@Override
	public void save(SolidifiedRequest thing, CompoundTag tag) {
		tag.put("request", thing.save());
	}
	
	@Override
	public Optional<SolidifiedRequest> tryLoad(CompoundTag tag) {
		return SolidifiedRequest.tryLoad(tag.getCompound("request"));
	}
	
	@Override
	public SolidifiedRequest defaultValue() {
		return SolidifiedRequest.EMPTY;
	}
	
	@Override
	public TicketItem<SolidifiedRequest> ticketItem() {
		return IncItems.SOLIDIFIED_REQUEST_TICKET;
	}
	
	@Override
	public TicketConjurerItem<SolidifiedRequest> conjurerItem() {
		return IncItems.SOLIDIFIED_REQUEST_CONJURER;
	}
	
	@Override
	public int color(SolidifiedRequest thing) {
		return 0x66FF33;
	}
	
	@Override
	public int signal(SolidifiedRequest thing) {
		return thing.signalStrength();
	}
	
	@Override
	public Component describe(SolidifiedRequest thing) {
		return thing.toComponent();
	}
	
	@Override
	public SolidifiedRequest parse(String message, ItemStack otherHand) {
		return RequestParser.parseRequest(message, otherHand);
	}
}
