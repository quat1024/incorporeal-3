package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

/**
 * A corporea request matcher that only matches things its inner matcher doesn't.
 */
public record InvertedCorporeaRequestMatcher(ICorporeaRequestMatcher inner) implements ICorporeaRequestMatcher {
	public static ICorporeaRequestMatcher invert(ICorporeaRequestMatcher other) {
		//Avoid inverting twice in this simple case
		if(other instanceof InvertedCorporeaRequestMatcher twoWrongsMakeARight) return twoWrongsMakeARight.inner;
		else return new InvertedCorporeaRequestMatcher(other);
	}
	
	@Override
	public boolean test(ItemStack stack) {
		return !inner.test(stack);
	}
	
	@Override
	public void writeToNBT(CompoundTag tag) {
		tag.put("inner", MatcherUtils.save(inner));
	}
	
	public static InvertedCorporeaRequestMatcher readFromNBT(CompoundTag tag) {
		//todo: don't throw
		return new InvertedCorporeaRequestMatcher(MatcherUtils.tryLoad(tag.getCompound("inner")).orElseThrow());
	}
	
	@Override
	public Component getRequestName() {
		return new TextComponent("!").withStyle(ChatFormatting.RED).append(inner.getRequestName());
	}
}
