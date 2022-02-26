package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.client.IncClientBlockProperties;
import agency.highlysuspect.incorporeal.client.IncClientEntityProperties;
import agency.highlysuspect.incorporeal.client.IncClientItemProperties;
import agency.highlysuspect.incorporeal.client.IncClientLayerDefinitions;
import agency.highlysuspect.incorporeal.client.IncClientNetwork;
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
		IncClientItemProperties.registerPropertyOverrides((item, id, prop) -> FabricModelPredicateProviderRegistry.register(item.asItem(), id, prop));
		
		//(block/)entity model layer definitions (new 1.18 thing I think?)
		IncClientLayerDefinitions.register((mll, sup) -> EntityModelLayerRegistry.registerModelLayer(mll, sup::get));
		
		//block render layers
		IncClientBlockProperties.registerRenderTypes(BlockRenderLayerMap.INSTANCE::putBlock);
		
		//block entity renderers, entity renderers, & builtin item renderers
		IncClientBlockProperties.registerBlockEntityRenderers(BlockEntityRendererRegistry::register);
		IncClientEntityProperties.registerEntityRenderers(EntityRendererRegistry::register);
		IncClientItemProperties.BE_ITEM_RENDERER_FACTORIES.forEach((block, teisrMaker) -> {
			//From Botania. Stands for "Tile Entity Item Stack Renderer", a Forge anachronism.
			TEISR teisr = teisrMaker.apply(block);
			BuiltinItemRendererRegistry.INSTANCE.register(block, teisr::render);
		});
		
		//wand HUD capability
		IncClientBlockProperties.registerWandHudCaps((factory, types) -> BotaniaFabricClientCapabilities.WAND_HUD.registerForBlockEntities((be, c) -> factory.apply(be), types));
		
		//client half of the network channel
		ClientPlayNetworking.registerGlobalReceiver(IncFabric.NETWORK_ID, (client, handler, buf, responseSender) -> IncClientNetwork.handle(buf));
		IncClientNetwork.initialize();
	}
}
