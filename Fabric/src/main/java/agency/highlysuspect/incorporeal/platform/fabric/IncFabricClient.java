package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.client.IncClientNetwork;
import agency.highlysuspect.incorporeal.client.IncClientProperties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import vazkii.botania.api.BotaniaFabricClientCapabilities;
import vazkii.botania.client.render.tile.TEISR;

public class IncFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//item property overrides
		IncClientProperties.registerPropertyOverrides((item, id, prop) -> FabricModelPredicateProviderRegistry.register(item.asItem(), id, prop));
		
		//block render layers
		IncClientProperties.registerRenderTypes(BlockRenderLayerMap.INSTANCE::putBlock);
		
		//block entity renderers, entity renderers, & builtin item renderers
		IncClientProperties.registerBlockEntityRenderers(BlockEntityRendererRegistry::register);
		IncClientProperties.registerEntityRenderers(EntityRendererRegistry::register);
		IncClientProperties.BE_ITEM_RENDERER_FACTORIES.forEach((block, teisrMaker) -> {
			//From Botania. Stands for "Tile Entity Item Stack Renderer", a Forge anachronism.
			TEISR teisr = teisrMaker.apply(block);
			BuiltinItemRendererRegistry.INSTANCE.register(block, teisr::render);
		});
		
		//wand HUD capability
		IncClientProperties.registerWandHudCaps((factory, types) -> BotaniaFabricClientCapabilities.WAND_HUD.registerForBlockEntities((be, c) -> factory.apply(be), types));
		
		//client half of the network channel
		ClientPlayNetworking.registerGlobalReceiver(IncFabric.NETWORK_ID, (client, handler, buf, responseSender) -> IncClientNetwork.handle(buf));
		IncClientNetwork.initialize();
	}
}
