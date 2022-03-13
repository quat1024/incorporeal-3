package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.corporea.AndingCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.MatcherUtils;
import net.minecraft.nbt.CompoundTag;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.List;
import java.util.Optional;

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
	public ICorporeaRequestMatcher sum(ICorporeaRequestMatcher... inputs) {
		return AndingCorporeaRequestMatcher.combine(List.of(inputs));
	}
}
