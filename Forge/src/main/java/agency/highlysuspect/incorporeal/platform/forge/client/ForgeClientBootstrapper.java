package agency.highlysuspect.incorporeal.platform.forge.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.client.IncClientNetwork;
import agency.highlysuspect.incorporeal.client.IncClientProperties;
import agency.highlysuspect.incorporeal.platform.IncClientBootstrapper;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.api.BotaniaForgeClientCapabilities;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.forge.CapabilityUtil;

import java.util.function.Function;

public class ForgeClientBootstrapper implements IncClientBootstrapper {
	@Override
	public void registerItemPropertyOverrides() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((ModelRegistryEvent e) -> {
			IncClientProperties.registerPropertyOverrides((item, id, prop) -> ItemProperties.register(item.asItem(), id, prop));
		});
	}
	
	@Override
	public void registerBlockRenderLayers() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((ModelRegistryEvent e) -> {
			IncClientProperties.registerRenderTypes(ItemBlockRenderTypes::setRenderLayer);
		});
	}
	
	@Override
	public void registerColorProviders() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((ColorHandlerEvent.Block e) -> IncClientProperties.registerBlockColorProviders(e.getBlockColors()::register));
		FMLJavaModLoadingContext.get().getModEventBus().addListener((ColorHandlerEvent.Item e) -> IncClientProperties.registerItemColorProviders(e.getItemColors()::register));
	}
	
	@Override
	public void registerBlockEntityRenderers() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((EntityRenderersEvent.RegisterRenderers e) -> {
			IncClientProperties.registerBlockEntityRenderers(e::registerBlockEntityRenderer);
		});
	}
	
	@Override
	public void registerEntityRenderers() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((EntityRenderersEvent.RegisterRenderers e) -> {
			IncClientProperties.registerEntityRenderers(e::registerEntityRenderer);
		});
	}
	
	@Override
	public void registerItemRenderers() {
		//See ForgeCoolRenderersMixin.
	}
	
	@Override
	public void registerClientCapabilities() {
		MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, (AttachCapabilitiesEvent<BlockEntity> e) -> {
			BlockEntity be = e.getObject();
			if(IncClientProperties.WAND_HUD_MAKERS.containsKey(be.getType())) {
				Function<BlockEntity, IWandHUD> maker = IncClientProperties.WAND_HUD_MAKERS.get(be.getType());
				e.addCapability(Inc.id("wand_hud"), CapabilityUtil.makeProvider(BotaniaForgeClientCapabilities.WAND_HUD, maker.apply(be)));
			}
		});
	}
	
	@Override
	public void registerServerToClientNetworkChannelReceiver() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent shit) -> IncClientNetwork.initialize());
	}
}
