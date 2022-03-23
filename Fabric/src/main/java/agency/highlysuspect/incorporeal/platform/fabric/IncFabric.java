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
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.botania.api.BotaniaFabricCapabilities;
import vazkii.botania.api.corporea.CorporeaIndexRequestCallback;
import vazkii.botania.api.mana.IManaReceiver;

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
		
		//entity types
		IncEntityTypes.register((type, name) -> Registry.register(Registry.ENTITY_TYPE, name, type));
		IncEntityTypes.registerAttributes(FabricDefaultAttributeRegistry::register);
		
		//sound events
		IncSounds.register((sound, name) -> Registry.register(Registry.SOUND_EVENT, name, sound));
		
		//Capabilities
		registerBotaniaCapabilities();
		registerFabricCapabilities();
		
		//computer stuff
		Inc.registerComputerStuff();
		
		incInit = true;
		afterBotania();
	}
	
	public void registerBotaniaCapabilities() {
		//Block entities that self-implement IManaReceiver
		for(BlockEntityType<? extends IManaReceiver> manaReceiverType : IncBlockEntityTypes.SELF_MANA_RECEIVER_BLOCK_ENTITY_TYPES) {
			BotaniaFabricCapabilities.MANA_RECEIVER.registerSelf(manaReceiverType);
		}
		
		//CoordBoundItem
		IncItems.COORD_BOUND_ITEM_MAKERS.forEach((item, maker) ->
			BotaniaFabricCapabilities.COORD_BOUND_ITEM.registerForItems((stack, context) -> maker.apply(stack), item));
	}
	
	//Kept in a separate method mainly so I can annotate it with this:
	@SuppressWarnings("UnstableApiUsage")
	public void registerFabricCapabilities() {
		//Add an item handler capability to the Ender Soul Core
		ItemStorage.SIDED.registerForBlockEntity(EnderSoulCoreStorage::new, IncBlockEntityTypes.ENDER_SOUL_CORE);
		
		//Add an item handler capability to the Red String Constrictor
		//(This line has no equiv on Forge because it's done inside ForgeRedStringConstrictorBlockEntity)
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
			Inc.afterBotania();
			
			didLateInit = true;
		} else {
			Inc.LOGGER.info("Not yet, gentle one.");
		}
	}
}
