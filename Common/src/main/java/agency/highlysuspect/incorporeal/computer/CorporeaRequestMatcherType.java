package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.MatcherUtils;
import agency.highlysuspect.incorporeal.corporea.RequestParser;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import agency.highlysuspect.incorporeal.item.TicketItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;

import java.util.Optional;

/**
 * A DataType representing corporea request matchers.
 */
public class CorporeaRequestMatcherType implements DataType<CorporeaRequestMatcher> {
	@Override
	public void save(CorporeaRequestMatcher thing, CompoundTag tag) {
		//Nest it inside a further tag "matcher" because uhh, matcherutils also uses the key "type"
		//This has caused 10000000 bugs and its ass to debug every single time
		tag.put("matcher", MatcherUtils.save(thing));
	}
	
	@Override
	public Optional<CorporeaRequestMatcher> tryLoad(CompoundTag tag) {
		return MatcherUtils.tryLoad(tag.getCompound("matcher"));
	}
	
	@Override
	public CorporeaRequestMatcher defaultValue() {
		return EmptyCorporeaRequestMatcher.INSTANCE;
	}
	
	@Override
	public TicketItem<CorporeaRequestMatcher> ticketItem() {
		return IncItems.MATCHER_TICKET;
	}
	
	@Override
	public TicketConjurerItem<CorporeaRequestMatcher> conjurerItem() {
		return IncItems.MATCHER_CONJURER;
	}
	
	@Override
	public int color(CorporeaRequestMatcher thing) {
		return 0x6270f3;
	}
	
	@Override
	public int signal(CorporeaRequestMatcher thing) {
		if(thing == EmptyCorporeaRequestMatcher.INSTANCE || thing == CorporeaRequestMatcher.Dummy.INSTANCE) return 0;
		else return 15;
	}
	
	@Override
	public Component describe(CorporeaRequestMatcher thing) {
		return thing.getRequestName();
	}
	
	@Override
	public CorporeaRequestMatcher parse(String message, ItemStack otherHand) {
		return RequestParser.parseMatcher(message, otherHand);
	}
}
