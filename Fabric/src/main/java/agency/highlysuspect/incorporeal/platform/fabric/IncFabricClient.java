package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.client.IncClientBlockProperties;
import agency.highlysuspect.incorporeal.client.IncClientItemProperties;
import agency.highlysuspect.incorporeal.client.IncClientModelDefinitions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

public class IncFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//item property overrides
		IncClientItemProperties.registerPropertyOverrides((item, id, prop) -> FabricModelPredicateProviderRegistry.register(item.asItem(), id, prop));
		
		//(block/)entity model definitions (new 1.18 thing I think?)
		IncClientModelDefinitions.register((mll, sup) -> EntityModelLayerRegistry.registerModelLayer(mll, sup::get));
		
		//block render layers
		IncClientBlockProperties.registerRenderTypes(BlockRenderLayerMap.INSTANCE::putBlock);
		
		//block entity renderers
		IncClientBlockProperties.registerBlockEntityRenderers(BlockEntityRendererRegistry::register);
	}
}
