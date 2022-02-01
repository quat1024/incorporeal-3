package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.client.IncBlockEntityRenderers;
import agency.highlysuspect.incorporeal.client.IncItemProperties;
import agency.highlysuspect.incorporeal.client.model.IncModelDefinitions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

public class IncFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//item property overrides
		IncItemProperties.register((item, id, prop) -> FabricModelPredicateProviderRegistry.register(item.asItem(), id, prop));
		
		//(block/)entity model definitions (new 1.18 thing I think?)
		IncModelDefinitions.register((mll, sup) -> EntityModelLayerRegistry.registerModelLayer(mll, sup::get));
		
		//block entity renderers
		IncBlockEntityRenderers.register(BlockEntityRendererRegistry::register);
	}
}
