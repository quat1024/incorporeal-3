package agency.highlysuspect.incorporeal.mixin;

import net.minecraft.data.models.model.TextureSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Moyang and their private constuctors.
 */
@Mixin(TextureSlot.class)
public interface TextureSlotAccessor {
	@Invoker("create") static TextureSlot inc$create(String name) {
		throw new IllegalStateException("dorp");
	}
}
