package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncCommands;
import agency.highlysuspect.incorporeal.IncSounds;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.client.IncClient;
import agency.highlysuspect.incorporeal.corporea.PlayerHeadHandler;
import agency.highlysuspect.incorporeal.IncEntityTypes;
import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.platform.ConfigBuilder;
import agency.highlysuspect.incorporeal.platform.IncBootstrapper;
import agency.highlysuspect.incorporeal.platform.forge.block.entity.EnderSoulCoreItemHandler;
import agency.highlysuspect.incorporeal.platform.forge.config.ForgeConfigBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.RegisterEvent;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.corporea.CorporeaIndexRequestEvent;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.forge.CapabilityUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IncBootstrapForge implements IncBootstrapper {
	@Override
	public ConfigBuilder createConfigBuilder(String filename) {
		return new ForgeConfigBuilder(filename);
	}
	
	//Paste from botania
	private static <T> void bind(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> {
			if (registry.equals(event.getRegistryKey())) {
				source.accept((t, rl) -> event.register(registry, rl, () -> t));
			}
		});
	}
	
	@Override
	public void registerBlocks() {
		bind(Registry.BLOCK_REGISTRY, IncBlocks::register);
	}
	
	@Override
	public void registerBlockEntityTypes() {
		bind(Registry.BLOCK_ENTITY_TYPE_REGISTRY, IncBlockEntityTypes::register);
	}
	
	@Override
	public void registerItems() {
		bind(Registry.ITEM_REGISTRY, IncItems::register);
	}
	
	@Override
	public void registerEntityTypes() {
		bind(Registry.ENTITY_TYPE_REGISTRY, IncEntityTypes::register);
	}
	
	@Override
	public void registerEntityAttributes() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((EntityAttributeCreationEvent e) ->
			IncEntityTypes.registerAttributes((type, builder) -> e.put(type, builder.build()))); //what
	}
	
	@Override
	public void registerSoundEvents() {
		bind(Registry.SOUND_EVENT_REGISTRY, IncSounds::register);
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
			
			//Block entities that self-implement IManaReceiver
			if(IncBlockEntityTypes.SELF_MANA_RECEIVER_BLOCK_ENTITY_TYPES.contains(be.getType()) && be instanceof ManaReceiver receiver) {
				event.addCapability(Inc.id("mana_receiver"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_RECEIVER, receiver));
			}
			
			//Block entities that self-implement IWandable
			if(IncBlockEntityTypes.SELF_WANDABLE_BLOCK_ENTITY_TYPES.contains(be.getType()) && be instanceof Wandable wandable) {
				event.addCapability(Inc.id("wandable"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.WANDABLE, wandable));
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
					IncItems.COORD_BOUND_ITEM_MAKERS.get(item).apply(stack)
				));
			}
			
			//IManaItem
			if(IncItems.MANA_ITEM_MAKERS.containsKey(item)) {
				event.addCapability(Inc.id("mana_item"), CapabilityUtil.makeProvider(
					BotaniaForgeCapabilities.MANA_ITEM,
					IncItems.MANA_ITEM_MAKERS.get(item).apply(stack)
				));
			}
		});
	}
	
	@Override
	public void registerCommands() {
		MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) ->
			IncCommands.register(e.getDispatcher(), e.getBuildContext(), e.getCommandSelection()));
	}
	
	@Override
	public void endSelfInit() {
		//On Fabric, calling onInitializeClient is done through a client entrypoint.
		//Forge doesn't have client entrypoints, so I will do it here instead.
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> IncClient.INSTANCE::onInitializeClient);
		
		//Ok so Forge ostensibly has mod load ordering, but it also has parallel-dispatch events, so who fucking knows anymore!
		//Loading after FMLCommonSetupEvent should be good enough?? i guess??
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) ->
			e.enqueueWork(() -> {
				//this bit puts it on the singlethreaded pool, after all the normal CommonSetup stuff happens
				Inc.INSTANCE.markBotaniaAsDoneInitializing();
				//Also I need to do this in an enqueueWork because lmao thread safety
				Inc.INSTANCE.registerDispenserBehaviors();
			}));
	}
	
	@Override
	public void registerCorporeaIndexCallback() {
		MinecraftForge.EVENT_BUS.addListener((CorporeaIndexRequestEvent e) ->
			e.setCanceled(PlayerHeadHandler.onIndexRequest(e.getRequester(), e.getMatcher(), e.getRequestCount(), e.getIndexSpark())));
	}
	
	@Override
	public void registerRedstoneRootPlaceEvent() {
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			if(e.getEntity() == null || e.getLevel() == null || e.getEntity().isSpectator()) return;
			
			ItemStack held = e.getItemStack();
			if(held.getItem() != BotaniaItems.redstoneRoot) return;
			
			InteractionResult result = IncBlocks.REDSTONE_ROOT_CROP.hookRedstoneRootClick(e.getEntity(), e.getLevel(), e.getItemStack(), e.getHand(), e.getHitVec());
			if(result != InteractionResult.PASS) {
				e.setCanceled(true);
				e.setCancellationResult(result);
			}
		});
	}
}
