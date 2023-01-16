package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;

/**
 * A corporea request matcher that only matches things its inner matcher doesn't.
 */
public record InvertedCorporeaRequestMatcher(CorporeaRequestMatcher inner) implements CorporeaRequestMatcher {
	public static CorporeaRequestMatcher invert(CorporeaRequestMatcher other) {
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
		return new InvertedCorporeaRequestMatcher(MatcherUtils.tryLoad(tag.getCompound("inner")).orElse(Fallback.INSTANCE));
	}
	
	@Override
	public Component getRequestName() {
		return Component.literal("!").withStyle(ChatFormatting.RED).append(inner.getRequestName());
	}
	
	//The fallback matcher, used when the inner matcher of InvertedCorporeaRequestMatcher can't load from NBT.
	//(e.g. it belongs to an addon, and the addon was removed.)
	//
	//Hey, fun fact: I already have an EmptyCorporeaRequestMatcher that matches nothing.
	//But if I used that to replace matchers that fail to load inside InvertedCorporeaRequestMatcher,
	//it would end up with a matcher that matches everything :) that seems a bit silly.
	//
	//Botania API restrictions mean that in my readFromNBT method I must always return an InvertedCorporeaRequestMatcher.
	//So, here we are.
	public static class Fallback implements CorporeaRequestMatcher {
		public static final Fallback INSTANCE = new Fallback();
		
		@Override
		public boolean test(ItemStack stack) {
			return true;
		}
		
		@Override
		public void writeToNBT(CompoundTag tag) {
			//Nope
		}
		
		@Override
		public Component getRequestName() {
			return Component.translatable("incorporeal.full_matcher");
		}
	}
}
