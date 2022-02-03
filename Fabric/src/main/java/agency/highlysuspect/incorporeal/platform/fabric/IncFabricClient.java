package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.client.IncClientBlockProperties;
import agency.highlysuspect.incorporeal.client.IncClientItemProperties;
import agency.highlysuspect.incorporeal.client.IncClientLayerDefinitions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import vazkii.botania.client.render.tile.TEISR;

public class IncFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//item property overrides
		IncClientItemProperties.registerPropertyOverrides((item, id, prop) -> FabricModelPredicateProviderRegistry.register(item.asItem(), id, prop));
		
		//(block/)entity model definitions (new 1.18 thing I think?)
		IncClientLayerDefinitions.register((mll, sup) -> EntityModelLayerRegistry.registerModelLayer(mll, sup::get));
		
		//block render layers
		IncClientBlockProperties.registerRenderTypes(BlockRenderLayerMap.INSTANCE::putBlock);
		
		//block entity renderers & builtin item renderers
		IncClientBlockProperties.registerBlockEntityRenderers(BlockEntityRendererRegistry::register);
		IncClientItemProperties.BE_ITEM_RENDERER_FACTORIES.forEach((block, teisrMaker) -> {
			//From Botania. Stands for "Tile Entity Item Stack Renderer", a Forge anachronism.
			TEISR teisr = teisrMaker.apply(block);
			BuiltinItemRendererRegistry.INSTANCE.register(block, teisr::render);
		});
	}
}
