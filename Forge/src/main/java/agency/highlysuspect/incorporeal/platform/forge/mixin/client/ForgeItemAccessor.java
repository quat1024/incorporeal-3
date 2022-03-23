package agency.highlysuspect.incorporeal.platform.forge.mixin.client;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ForgeItemAccessor {
	@Accessor(
		value = "renderProperties",
		remap = false //Forge extension
	)
	void inc$setRenderProperties(Object renderProperties);
}
