package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.client.IncClientBlockProperties;
import agency.highlysuspect.incorporeal.client.IncClientItemProperties;
import agency.highlysuspect.incorporeal.client.IncClientLayerDefinitions;
import agency.highlysuspect.incorporeal.client.IncClientNetwork;
import com.google.common.base.Suppliers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.api.BotaniaForgeClientCapabilities;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.forge.CapabilityUtil;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class IncForgeClient {
	public static void init() {
		IEventBus yes = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus no = MinecraftForge.EVENT_BUS; //i love forge it's really not over engineered at all
		
		yes.addListener((ModelRegistryEvent e) -> {
			//item property overrides
			IncClientItemProperties.registerPropertyOverrides((item, id, prop) -> ItemProperties.register(item.asItem(), id, prop));
			
			//block render layers
			IncClientBlockProperties.registerRenderTypes(ItemBlockRenderTypes::setRenderLayer);
		});
		
		yes.addListener((EntityRenderersEvent.RegisterRenderers e) -> {
			//block entity renderers
			IncClientBlockProperties.registerBlockEntityRenderers(e::registerBlockEntityRenderer);
			
			//(builtin item renderers handled with a different method)
		});
		
		yes.addListener((EntityRenderersEvent.RegisterLayerDefinitions e) -> {
			//(block/)entity model layer definitions
			IncClientLayerDefinitions.register(e::registerLayerDefinition);
		});
		
		//Lazily copy pasted from Botania as usual
		no.addGenericListener(BlockEntity.class, (AttachCapabilitiesEvent<BlockEntity> e) -> {
			BlockEntity be = e.getObject();
			Function<BlockEntity, IWandHUD> makeWandHud = incWandHuds.get().get(be.getType());
			if(makeWandHud != null) {
				e.addCapability(Inc.botaniaId("wand_hud"), CapabilityUtil.makeProvider(BotaniaForgeClientCapabilities.WAND_HUD, makeWandHud.apply(be)));
			}
		});
		
		yes.addListener((FMLCommonSetupEvent shit) -> {
			IncClientNetwork.initialize();
		});
	}
	
	//Lazily copy pasted from Botania as usual
	private static final Supplier<Map<BlockEntityType<?>, Function<BlockEntity, IWandHUD>>> incWandHuds = Suppliers.memoize(() -> {
		Map<BlockEntityType<?>, Function<BlockEntity, IWandHUD>> map = new IdentityHashMap<>();
		IncClientBlockProperties.registerWandHudCaps((factory, types) -> {
			for(BlockEntityType<?> type : types) {
				map.put(type, factory);
			}
		});
		return map;
	});
}
