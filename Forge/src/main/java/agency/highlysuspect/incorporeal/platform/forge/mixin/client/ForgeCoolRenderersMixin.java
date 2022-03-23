package agency.highlysuspect.incorporeal.platform.forge.mixin.client;

import agency.highlysuspect.incorporeal.platform.IncBlockItemWithCoolRenderer;
import agency.highlysuspect.incorporeal.platform.IncItemWithCoolRenderer;
import agency.highlysuspect.incorporeal.platform.forge.client.IncForgeBlockEntityItemRendererHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

//self-mixin
@Mixin(value = {
	IncItemWithCoolRenderer.class,
	IncBlockItemWithCoolRenderer.class
})
public class ForgeCoolRenderersMixin extends Item {
	//Dummy constructor for mixin
	public ForgeCoolRenderersMixin(Properties dummy) {
		super(dummy);
	}
	
	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) { //Forge extension method
		IncForgeBlockEntityItemRendererHelper.initItem(consumer);
	}
}
