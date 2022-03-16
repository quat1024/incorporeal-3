package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

/**
 * An ICorporeaRequestMatcher that rudely refuses to recognize any items.
 */
public class EmptyCorporeaRequestMatcher implements ICorporeaRequestMatcher {
	public static final EmptyCorporeaRequestMatcher INSTANCE = new EmptyCorporeaRequestMatcher();
	
	private EmptyCorporeaRequestMatcher() {}
	
	@Override
	public boolean test(ItemStack stack) {
		return false; //Nope
	}
	
	@Override
	public void writeToNBT(CompoundTag tag) {
		//Nada
	}
	
	@Override
	public Component getRequestName() {
		return new TranslatableComponent("incorporeal.empty_matcher");
	}
}
