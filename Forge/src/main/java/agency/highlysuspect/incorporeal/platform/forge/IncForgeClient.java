package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.client.IncClientBlockProperties;
import agency.highlysuspect.incorporeal.client.IncClientItemProperties;
import agency.highlysuspect.incorporeal.client.IncClientLayerDefinitions;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class IncForgeClient {
	public static void init() {
		IEventBus yes = FMLJavaModLoadingContext.get().getModEventBus();
		
		yes.addListener((ModelRegistryEvent e) -> {
			IncClientItemProperties.registerPropertyOverrides((item, id, prop) -> ItemProperties.register(item.asItem(), id, prop));
			IncClientBlockProperties.registerRenderTypes(ItemBlockRenderTypes::setRenderLayer);
		});
		
		yes.addListener((EntityRenderersEvent.RegisterRenderers e) -> {
			IncClientBlockProperties.registerBlockEntityRenderers(e::registerBlockEntityRenderer);
		});
		
		yes.addListener((EntityRenderersEvent.RegisterLayerDefinitions e) -> {
			IncClientLayerDefinitions.register(e::registerLayerDefinition);
		});
	}
}
