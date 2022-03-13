package agency.highlysuspect.incorporeal.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.common.impl.corporea.CorporeaItemStackMatcher;

/**
 * Computermod needs to be able to read the ItemStack off of these kinds of corporea requests.
 * It's used when setting the displayed item of a Corporea Crystal Cube. They can only display requests for ItemStacks, not strings.
 */
@Mixin(CorporeaItemStackMatcher.class)
public interface CorporeaItemStackMatcherAccessor {
	@Accessor(value = "match", remap = false) ItemStack inc$getMatch();
}
