package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.client.IncBlockEntityRenderers;
import agency.highlysuspect.incorporeal.client.IncItemProperties;
import agency.highlysuspect.incorporeal.client.model.IncModelDefinitions;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class IncForgeClient {
	public static void init() {
		IEventBus yes = FMLJavaModLoadingContext.get().getModEventBus();
		
		yes.addListener((ModelRegistryEvent e) -> {
			IncItemProperties.register((item, id, prop) -> ItemProperties.register(item.asItem(), id, prop));
		});
		
		yes.addListener((EntityRenderersEvent.RegisterRenderers e) -> {
			IncBlockEntityRenderers.register(e::registerBlockEntityRenderer);
		});
		
		yes.addListener((EntityRenderersEvent.RegisterLayerDefinitions e) -> {
			IncModelDefinitions.register(e::registerLayerDefinition);
		});
	}
}
