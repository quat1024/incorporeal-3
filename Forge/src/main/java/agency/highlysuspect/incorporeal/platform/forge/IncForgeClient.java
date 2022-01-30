package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.client.IncBlockEntityRenderers;
import agency.highlysuspect.incorporeal.client.IncItemProperties;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class IncForgeClient {
	public static void init() {
		IEventBus pee = FMLJavaModLoadingContext.get().getModEventBus();
		
		pee.addListener((ModelRegistryEvent e) -> {
			IncItemProperties.register((item, id, prop) -> ItemProperties.register(item.asItem(), id, prop));
		});
		
		pee.addListener((EntityRenderersEvent.RegisterRenderers e) -> {
			IncBlockEntityRenderers.register(e::registerBlockEntityRenderer);
		});
	}
}
