package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncSounds;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.corporea.PlayerHeadHandler;
import agency.highlysuspect.incorporeal.IncEntityTypes;
import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.platform.IncBootstrapper;
import agency.highlysuspect.incorporeal.platform.fabric.block.entity.EnderSoulCoreStorage;
import agency.highlysuspect.incorporeal.platform.fabric.block.entity.FabricRedStringConstrictorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.botania.api.BotaniaFabricCapabilities;
import vazkii.botania.api.corporea.CorporeaIndexRequestCallback;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.function.BiConsumer;

public class IncBootstrapFabric implements IncBootstrapper {
	public static final ResourceLocation NETWORK_ID = new ResourceLocation(Inc.MODID, "net"); //Gotta put this somewhere
	
	private static <T> BiConsumer<T, ResourceLocation> createRegistrar(Registry<T> reg) {
		return (thing, name) -> Registry.register(reg, name, thing);
	}
	
	@Override
	public void registerBlocks() {
		IncBlocks.register(createRegistrar(Registry.BLOCK));
	}
	
	@Override
	public void registerBlockEntityTypes() {
		IncBlockEntityTypes.register(createRegistrar(Registry.BLOCK_ENTITY_TYPE));
	}
	
	@Override
	public void registerItems() {
		IncItems.register(createRegistrar(Registry.ITEM));
	}
	
	@Override
	public void registerEntityTypes() {
		IncEntityTypes.register(createRegistrar(Registry.ENTITY_TYPE));
	}
	
	@Override
	public void registerEntityAttributes() {
		IncEntityTypes.registerAttributes(FabricDefaultAttributeRegistry::register);
	}
	
	@Override
	public void registerSoundEvents() {
		IncSounds.register(createRegistrar(Registry.SOUND_EVENT));
	}
	
	@Override
	public void registerServerToClientNetworkChannelSender() {
		//On Fabric, you don't need to register network channels before sending messages to them.
	}
	
	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void registerCapabilities() {
		//Block entities that self-implement IManaReceiver
		for(BlockEntityType<? extends IManaReceiver> manaReceiverType : IncBlockEntityTypes.SELF_MANA_RECEIVER_BLOCK_ENTITY_TYPES) {
			BotaniaFabricCapabilities.MANA_RECEIVER.registerSelf(manaReceiverType);
		}
		
		//CoordBoundItem
		IncItems.COORD_BOUND_ITEM_MAKERS.forEach((item, maker) ->
			BotaniaFabricCapabilities.COORD_BOUND_ITEM.registerForItems((stack, context) -> maker.apply(stack), item));
		
		//Add an item handler capability to the Ender Soul Core
		ItemStorage.SIDED.registerForBlockEntity(EnderSoulCoreStorage::new, IncBlockEntityTypes.ENDER_SOUL_CORE);
		
		//Add an item handler capability to the Red String Constrictor
		//(This line has no equiv on Forge because it's done inside ForgeRedStringConstrictorBlockEntity)
		ItemStorage.SIDED.registerForBlockEntity(FabricRedStringConstrictorBlockEntity::getStorageWithDowncast, IncBlockEntityTypes.RED_STRING_CONSTRICTOR);
	}
	
	@Override
	public void endSelfInit() {
		//Nothing else to do.
	}
	
	@Override
	public void registerCorporeaIndexCallback() {
		CorporeaIndexRequestCallback.EVENT.register(PlayerHeadHandler::onIndexRequest);
	}
}
