package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncSounds;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.corporea.PlayerHeadHandler;
import agency.highlysuspect.incorporeal.entity.IncEntityTypes;
import agency.highlysuspect.incorporeal.item.FracturedSpaceRodItem;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.platform.fabric.mixin.FabricFabricCommonInitializerMixin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import vazkii.botania.api.BotaniaFabricCapabilities;
import vazkii.botania.api.corporea.CorporeaIndexRequestCallback;

public class IncFabric implements ModInitializer {
	public static final ResourceLocation NETWORK_ID = new ResourceLocation(Inc.MODID, "net");
	
	//tfw no mod load order
	public static boolean incInit = false;
	public static boolean botaniaInit = false;
	public static boolean didLateInit = false;
	
	@Override
	public void onInitialize() {
		//blocks
		IncBlocks.register((block, name) -> Registry.register(Registry.BLOCK, name, block));
		
		//block entity types
		IncBlockEntityTypes.register((type, name) -> Registry.register(Registry.BLOCK_ENTITY_TYPE, name, type));
		
		//items and item capabilities
		IncItems.register((item, name) -> Registry.register(Registry.ITEM, name, item));
		BotaniaFabricCapabilities.COORD_BOUND_ITEM.registerForItems((st, c) -> new FracturedSpaceRodItem.CoordBoundItem(st), IncItems.FRACTURED_SPACE_ROD);
		
		//entity types
		IncEntityTypes.register((type, name) -> Registry.register(Registry.ENTITY_TYPE, name, type));
		IncEntityTypes.registerAttributes(FabricDefaultAttributeRegistry::register);
		
		//sound events
		IncSounds.register((sound, name) -> Registry.register(Registry.SOUND_EVENT, name, sound));
		
		//Capabilities
		registerCapabilities();
		
		incInit = true;
		afterBotania();
	}
	
	@SuppressWarnings("UnstableApiUsage")
	public void registerCapabilities() {
		ItemStorage.SIDED.registerForBlockEntity(EnderSoulCoreStorage::new, IncBlockEntityTypes.ENDER_SOUL_CORE);
		ItemStorage.SIDED.registerForBlockEntity(FabricRedStringConstrictorBlockEntity::getStorageWithDowncast, IncBlockEntityTypes.RED_STRING_CONSTRICTOR);
	}
	
	/**
	 * Some Botania behaviors use lists that are checked top-to-bottom.
	 * In those cases, registration order may be important.
	 * This is a problem when there's no load-order in the frickin modloader lmao!
	 * @see FabricFabricCommonInitializerMixin
	 */
	public static void afterBotania() {
		Inc.LOGGER.warn("Fabric post-botania init: incorporeal {}, botania {}", incInit, botaniaInit);
		if(incInit && botaniaInit) {
			if(didLateInit) throw new IllegalStateException("Already post-botania initialized");
			Inc.LOGGER.info("It is time.");
			
			//that one corporea event
			CorporeaIndexRequestCallback.EVENT.register(PlayerHeadHandler::onIndexRequest);
			
			//some other stuff (not different between loaders)
			Inc.registerExtraThings();
			
			didLateInit = true;
		} else {
			Inc.LOGGER.info("Not yet, gentle one.");
		}
	}
}
