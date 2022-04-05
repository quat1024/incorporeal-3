package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.MatcherUtils;
import agency.highlysuspect.incorporeal.corporea.RequestParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Optional;

/**
 * A DataType representing corporea request matchers.
 */
public class CorporeaRequestMatcherType implements DataType<ICorporeaRequestMatcher> {
	@Override
	public void save(ICorporeaRequestMatcher thing, CompoundTag tag) {
		//Nest it inside a further tag "matcher" because uhh, matcherutils also uses the key "type"
		//This has caused 10000000 bugs and its ass to debug every single time
		tag.put("matcher", MatcherUtils.save(thing));
	}
	
	@Override
	public Optional<ICorporeaRequestMatcher> tryLoad(CompoundTag tag) {
		return MatcherUtils.tryLoad(tag.getCompound("matcher"));
	}
	
	@Override
	public int color(ICorporeaRequestMatcher thing) {
		return 0x6270f3;
	}
	
	@Override
	public int signal(ICorporeaRequestMatcher thing) {
		if(thing == EmptyCorporeaRequestMatcher.INSTANCE || thing == ICorporeaRequestMatcher.Dummy.INSTANCE) return 0;
		else return 15;
	}
	
	@Override
	public Component describe(ICorporeaRequestMatcher thing) {
		return thing.getRequestName();
	}
	
	@Override
	public ICorporeaRequestMatcher parse(String message, ItemStack otherHand) {
		return RequestParser.parseMatcher(message, otherHand);
	}
}
