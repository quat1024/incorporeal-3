package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.MatcherUtils;
import net.minecraft.nbt.CompoundTag;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Optional;

/**
 * A DataType representing corporea request matchers.
 */
public class CorporeaRequestMatcherType implements DataType<ICorporeaRequestMatcher> {
	@Override
	public void save(ICorporeaRequestMatcher thing, CompoundTag tag) {
		tag.put("matcher", MatcherUtils.save(thing));
	}
	
	@Override
	public Optional<ICorporeaRequestMatcher> tryLoad(CompoundTag tag) {
		return MatcherUtils.tryLoad(tag);
	}
	
	@Override
	public int color(ICorporeaRequestMatcher thing) {
		return 0x7722FF;
	}
	
	@Override
	public int signal(ICorporeaRequestMatcher thing) {
		if(thing == EmptyCorporeaRequestMatcher.INSTANCE || thing == ICorporeaRequestMatcher.Dummy.INSTANCE) return 0;
		else return 15;
	}
}
