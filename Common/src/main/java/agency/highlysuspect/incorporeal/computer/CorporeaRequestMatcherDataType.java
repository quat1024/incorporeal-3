package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.AndingCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.MatcherUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.List;

public class CorporeaRequestMatcherDataType implements DataType<ICorporeaRequestMatcher> {
	@Override
	public Class<ICorporeaRequestMatcher> typeClass() {
		return ICorporeaRequestMatcher.class;
	}
	
	@Override
	public ResourceLocation id() {
		return Inc.id("matcher");
	}
	
	@Override
	public void save(ICorporeaRequestMatcher thing, CompoundTag tag) {
		tag.put("matcher", MatcherUtils.toTag(thing));
	}
	
	@Override
	public ICorporeaRequestMatcher load(CompoundTag tag) {
		return MatcherUtils.tryFromTag(tag).orElseThrow(); //TODO not the best way to handle that
	}
	
	@Override
	public ICorporeaRequestMatcher add(ICorporeaRequestMatcher... inputs) {
		return AndingCorporeaRequestMatcher.combine(List.of(inputs));
	}
}
