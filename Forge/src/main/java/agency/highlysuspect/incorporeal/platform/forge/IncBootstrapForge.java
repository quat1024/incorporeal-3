package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncSounds;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.client.IncClient;
import agency.highlysuspect.incorporeal.corporea.PlayerHeadHandler;
import agency.highlysuspect.incorporeal.IncEntityTypes;
import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.platform.IncBootstrapper;
import agency.highlysuspect.incorporeal.platform.forge.block.entity.EnderSoulCoreItemHandler;
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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.corporea.CorporeaIndexRequestEvent;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.forge.CapabilityUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IncBootstrapForge implements IncBootstrapper {
	//Paste from botania
	private static <T extends IForgeRegistryEntry<T>> void bind(IForgeRegistry<T> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(registry.getRegistrySuperType(),
			(RegistryEvent.Register<T> event) -> {
				IForgeRegistry<T> forgeRegistry = event.getRegistry();
				source.accept((thing, name) -> {
					thing.setRegistryName(name);
					forgeRegistry.register(thing);
				});
			});
	}
	
	@Override
	public void registerBlocks() {
		bind(ForgeRegistries.BLOCKS, IncBlocks::register);
	}
	
	@Override
	public void registerBlockEntityTypes() {
		bind(ForgeRegistries.BLOCK_ENTITIES, IncBlockEntityTypes::register);
	}
	
	@Override
	public void registerItems() {
		bind(ForgeRegistries.ITEMS, IncItems::register);
	}
	
	@Override
	public void registerEntityTypes() {
		bind(ForgeRegistries.ENTITIES, IncEntityTypes::register);
	}
	
	@Override
	public void registerEntityAttributes() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((EntityAttributeCreationEvent e) ->
			IncEntityTypes.registerAttributes((type, builder) -> e.put(type, builder.build()))); //what
	}
	
	@Override
	public void registerSoundEvents() {
		bind(ForgeRegistries.SOUND_EVENTS, IncSounds::register);
	}
	
	@Override
	public void registerServerToClientNetworkChannelSender() {
		IncForgeNetworking.init();
	}
	
	@Override
	public void registerCapabilities() {
		//Block entity caps
		MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, (AttachCapabilitiesEvent<BlockEntity> event) -> {
			BlockEntity be = event.getObject();
			
			//ManaReceiver for all block entity types that implement it on themself
			if(IncBlockEntityTypes.SELF_MANA_RECEIVER_BLOCK_ENTITY_TYPES.contains(be.getType()) && be instanceof IManaReceiver receiver) {
				event.addCapability(Inc.id("mana_receiver"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_RECEIVER, receiver));
			}
			
			//Add an item handler capability to the Ender Soul Core
			if(be.getType() == IncBlockEntityTypes.ENDER_SOUL_CORE && be instanceof EnderSoulCoreBlockEntity esc) {
				event.addCapability(Inc.id("inventory"), CapabilityUtil.makeProvider(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
					new EnderSoulCoreItemHandler(esc)));
			}
		});
		
		//Itemstack caps
		MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, (AttachCapabilitiesEvent<ItemStack> event) -> {
			ItemStack stack = event.getObject();
			Item item = stack.getItem();
			
			//CoordBoundItem
			if(IncItems.COORD_BOUND_ITEM_MAKERS.containsKey(item)) {
				event.addCapability(Inc.id("coord_bound_item"), CapabilityUtil.makeProvider(
					BotaniaForgeCapabilities.COORD_BOUND_ITEM,
					IncItems.COORD_BOUND_ITEM_MAKERS.get(item).apply(stack))
				);
			}
		});
	}
	
	@Override
	public void endSelfInit() {
		//On Fabric, calling onInitializeClient is done through a client entrypoint.
		//Forge doesn't have client entrypoints, so I will do it here instead.
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> IncClient.INSTANCE::onInitializeClient);
		
		//Ok so Forge ostensibly has mod load ordering, but it also has parallel-dispatch events, so who fucking knows anymore!
		//Loading after FMLCommonSetupEvent should be good enough?? i guess??
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) ->
			e.enqueueWork(() -> //this bit puts it on the singlethreaded pool, after all the normal CommonSetup stuff happens
				Inc.INSTANCE.markBotaniaAsDoneInitializing()));
	}
	
	@Override
	public void registerCorporeaIndexCallback() {
		MinecraftForge.EVENT_BUS.addListener((CorporeaIndexRequestEvent e) ->
			e.setCanceled(PlayerHeadHandler.onIndexRequest(e.getRequester(), e.getMatcher(), e.getRequestCount(), e.getIndexSpark())));
	}
}