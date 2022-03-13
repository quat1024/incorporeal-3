package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An "anding" corporea request matcher, because it takes a bunch of child requests and only matches the item if all the children do.
 */
public record AndingCorporeaRequestMatcher(List<ICorporeaRequestMatcher> others) implements ICorporeaRequestMatcher {
	public static ICorporeaRequestMatcher combine(Collection<ICorporeaRequestMatcher> others) {
		//remove all null/empty matchers, flatten layers of CompoundCorporeaRequestMatchers
		List<ICorporeaRequestMatcher> collated = others.stream()
			.filter(matcher -> matcher != null && matcher != EmptyCorporeaRequestMatcher.INSTANCE)
			.flatMap(matcher -> {
				if(matcher instanceof AndingCorporeaRequestMatcher compound) return compound.others.stream();
				else return Stream.of(matcher);
			}).toList();
		
		//try not to add additional layers of wrapping if i don't need to
		if(collated.size() == 0) return EmptyCorporeaRequestMatcher.INSTANCE;
		if(collated.size() == 1) return collated.iterator().next();
		return new AndingCorporeaRequestMatcher(collated);
	}
	
	@Override
	public boolean test(ItemStack stack) {
		//big "and" operation
		for(ICorporeaRequestMatcher matcher : others) {
			if(!matcher.test(stack)) return false;
		}
		return true;
	}
	
	@Override
	public void writeToNBT(CompoundTag tag) {
		ListTag list = new ListTag();
		for(ICorporeaRequestMatcher matcher : others) list.add(MatcherUtils.save(matcher));
		tag.put("compound", list);
	}
	
	public static AndingCorporeaRequestMatcher readFromNbt(CompoundTag tag) {
		return new AndingCorporeaRequestMatcher(tag.getList("compound", 10).stream()
			.filter(t -> t instanceof CompoundTag)
			.map(t -> (CompoundTag) t)
			.map(MatcherUtils::tryLoad)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toList());
	}
	
	@Override
	public Component getRequestName() {
		return ComponentUtils.formatList(others, new TextComponent(" & "), ICorporeaRequestMatcher::getRequestName);
	}
}
