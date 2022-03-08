package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncSounds;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.corporea.PlayerHeadHandler;
import agency.highlysuspect.incorporeal.entity.IncEntityTypes;
import agency.highlysuspect.incorporeal.item.FracturedSpaceRodItem;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.corporea.CorporeaIndexRequestEvent;
import vazkii.botania.forge.CapabilityUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod("incorporeal")
public class IncForge {
	public IncForge() {
		//blocks
		bind(ForgeRegistries.BLOCKS, IncBlocks::register);
		
		//items and item capabilities
		bind(ForgeRegistries.ITEMS, IncItems::register);
		MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, IncForge::itemCapabilities);
		
		//block entity types
		bind(ForgeRegistries.BLOCK_ENTITIES, IncBlockEntityTypes::register);
		
		//entities
		bind(ForgeRegistries.ENTITIES, IncEntityTypes::register);
		FMLJavaModLoadingContext.get().getModEventBus().addListener((EntityAttributeCreationEvent e) ->
			IncEntityTypes.registerAttributes((type, builder) -> e.put(type, builder.build()))); //what
		
		//sound events
		bind(ForgeRegistries.SOUND_EVENTS, IncSounds::register);
		
		//that one corporea event
		MinecraftForge.EVENT_BUS.addListener((CorporeaIndexRequestEvent e) ->
			e.setCanceled(PlayerHeadHandler.onIndexRequest(e.getRequester(), e.getMatcher(), e.getRequestCount(), e.getIndexSpark())));
		
		//I gotta admit. Fabric's "client entrypoint" scheme is a lot nicer than forge's "proxy" pattern
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> IncForgeClient::init);
		
		//Awful networking crap that i dont need any of on fabric because fabric has a reasonable networking api
		IncForgeNetworking.init();
		
		//block entity capabilities
		MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, IncForge::blockEntityCapabilities);
		
		//some other stuff (not different between loaders)
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) -> Inc.registerExtraThings());
	}
	
	//botania copypasterino !
	private static <T extends IForgeRegistryEntry<T>> void bind(IForgeRegistry<T> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(registry.getRegistrySuperType(),
			(RegistryEvent.Register<T> event) -> {
				IForgeRegistry<T> forgeRegistry = event.getRegistry();
				source.accept((t, rl) -> {
					t.setRegistryName(rl);
					forgeRegistry.register(t);
				});
			});
	}
	
	private static void blockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
		BlockEntity be = event.getObject();
		if(be.getType() == IncBlockEntityTypes.ENDER_SOUL_CORE && be instanceof EnderSoulCoreBlockEntity esc) {
			event.addCapability(Inc.id("inventory"), CapabilityUtil.makeProvider(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				new EnderSoulCoreItemHandler(esc)));
		} else if(be.getType() == IncBlockEntityTypes.RED_STRING_CONSTRICTOR && be instanceof RedStringConstrictorBlockEntity rsc) {
			event.addCapability(Inc.id("magic"), new RedStringConstrictorCapProvider(rsc));
		}
	}
	
	private static void itemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject();
		if(stack.getItem() == IncItems.FRACTURED_SPACE_ROD) {
			event.addCapability(Inc.id("coord_bound_item"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.COORD_BOUND_ITEM,
				new FracturedSpaceRodItem.CoordBoundItem(stack)));
		}
	}
}
