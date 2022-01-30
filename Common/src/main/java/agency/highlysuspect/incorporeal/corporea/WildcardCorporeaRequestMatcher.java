package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

/**
 * Corporea request matcher that matches everything.
 * Implementation detail of LyingContainerCorporeaNode at the moment.
 */
public class WildcardCorporeaRequestMatcher implements ICorporeaRequestMatcher {
	public static final WildcardCorporeaRequestMatcher INSTANCE = new WildcardCorporeaRequestMatcher();
	
	@Override
	public boolean test(ItemStack stack) {
		return true;
	}
	
	@Override
	public void writeToNBT(CompoundTag tag) {
		//Nothing
	}
	
	@Override
	public Component getRequestName() {
		return new TranslatableComponent("incorporeal.request.anything");
	}
}
