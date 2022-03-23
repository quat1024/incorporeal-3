package agency.highlysuspect.incorporeal.platform.fabric.client;

import agency.highlysuspect.incorporeal.client.IncClientNetwork;
import agency.highlysuspect.incorporeal.client.IncClientProperties;
import agency.highlysuspect.incorporeal.platform.IncClientBootstrapper;
import agency.highlysuspect.incorporeal.platform.fabric.IncBootstrapFabric;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import vazkii.botania.api.BotaniaFabricClientCapabilities;
import vazkii.botania.client.render.tile.TEISR;

public class FabricClientBootstrapper implements IncClientBootstrapper {
	@Override
	public void registerItemPropertyOverrides() {
		IncClientProperties.registerPropertyOverrides((item, id, prop) -> FabricModelPredicateProviderRegistry.register(item.asItem(), id, prop));
	}
	
	@Override
	public void registerBlockRenderLayers() {
		IncClientProperties.registerRenderTypes(BlockRenderLayerMap.INSTANCE::putBlock);
	}
	
	@Override
	public void registerColorProviders() {
		IncClientProperties.registerBlockColorProviders(ColorProviderRegistry.BLOCK::register);
		IncClientProperties.registerItemColorProviders(ColorProviderRegistry.ITEM::register);
	}
	
	@Override
	public void registerBlockEntityRenderers() {
		IncClientProperties.registerBlockEntityRenderers(BlockEntityRendererRegistry::register);
	}
	
	@Override
	public void registerEntityRenderers() {
		IncClientProperties.registerEntityRenderers(EntityRendererRegistry::register);
	}
	
	@Override
	public void registerItemRenderers() {
		IncClientProperties.BE_ITEM_RENDERER_FACTORIES.forEach((block, teisrMaker) -> {
			//From Botania. Stands for "Tile Entity Item Stack Renderer", a Forge anachronism.
			TEISR teisr = teisrMaker.apply(block);
			BuiltinItemRendererRegistry.INSTANCE.register(block, teisr::render);
		});
	}
	
	@Override
	public void registerClientCapabilities() {
		IncClientProperties.WAND_HUD_MAKERS.forEach((type, maker) ->
			BotaniaFabricClientCapabilities.WAND_HUD.registerForBlockEntities((be, context) -> maker.apply(be), type));
	}
	
	@Override
	public void registerServerToClientNetworkChannelReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(IncBootstrapFabric.NETWORK_ID, (client, handler, buf, responseSender) -> IncClientNetwork.handle(buf));
	}
}
