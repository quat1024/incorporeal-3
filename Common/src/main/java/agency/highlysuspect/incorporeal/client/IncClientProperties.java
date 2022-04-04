package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.block.entity.AbstractSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.IncEntityTypes;
import agency.highlysuspect.incorporeal.IncItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.client.render.ColorHandler;
import vazkii.botania.client.render.entity.EntityRenderers;
import vazkii.botania.client.render.tile.RenderTileRedString;
import vazkii.botania.client.render.tile.RenderTileSpecialFlower;
import vazkii.botania.network.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IncClientProperties {
	/// Dynamic item renderers ///
	
	public static final Map<Item, Supplier<MyDynamicItemRenderer>> MY_DYNAMIC_ITEM_RENDERERS = new HashMap<>();
	static {
		IncItems.UNSTABLE_CUBES.forEach((color, cube) ->
			MY_DYNAMIC_ITEM_RENDERERS.put(cube, () -> UnstableCubeRenderers.createItemRenderer(color)));
		MY_DYNAMIC_ITEM_RENDERERS.put(IncItems.ENDER_SOUL_CORE, () -> SoulCoreRenderers.createItemRenderer(IncBlocks.ENDER_SOUL_CORE.defaultBlockState()));
		MY_DYNAMIC_ITEM_RENDERERS.put(IncItems.POTION_SOUL_CORE, () -> SoulCoreRenderers.createItemRenderer(IncBlocks.POTION_SOUL_CORE.defaultBlockState()));
		MY_DYNAMIC_ITEM_RENDERERS.put(IncItems.SOUL_CORE_FRAME, () -> SoulCoreRenderers.createItemRendererForNamedModel(Inc.id("block/soul_core_frame")));
	}
	
	public static void registerExtraModelsToBake(Consumer<ResourceLocation> r) {
		r.accept(Inc.id("block/soul_core_frame"));
	}
	
	/// Block render layers ///
	
	public static void registerRenderTypes(BiConsumer<Block, RenderType> r) {
		RenderType cutout = RenderType.cutout();
		RenderType translucent = RenderType.translucent();
		
		r.accept(IncBlocks.NATURAL_REPEATER, cutout);
		r.accept(IncBlocks.NATURAL_COMPARATOR, cutout);
		r.accept(IncBlocks.REDSTONE_ROOT_CROP, cutout);
		
		r.accept(IncBlocks.ENDER_SOUL_CORE, translucent);
		r.accept(IncBlocks.POTION_SOUL_CORE, translucent);
		
		r.accept(IncBlocks.SANVOCALIA, cutout);
		r.accept(IncBlocks.SANVOCALIA_SMALL, cutout);
		r.accept(IncBlocks.FLOATING_SANVOCALIA, cutout);
		r.accept(IncBlocks.FLOATING_SANVOCALIA_SMALL, cutout);
		
		r.accept(IncBlocks.FUNNY, cutout);
		r.accept(IncBlocks.FUNNY_SMALL, cutout);
		r.accept(IncBlocks.FLOATING_FUNNY, cutout);
		r.accept(IncBlocks.FLOATING_FUNNY_SMALL, cutout);
		
		for(Block b : IncBlocks.COMPRESSED_TATERS.values()) r.accept(b, cutout);
		
		r.accept(IncBlocks.DATA_FUNNEL, translucent);
		r.accept(IncBlocks.DATASTONE_BLOCK, translucent);
		r.accept(IncBlocks.POINTED_DATASTONE, translucent);
	}
	
	/// Block entity renderers ///
	
	public static void registerBlockEntityRenderers(EntityRenderers.BERConsumer r) {
		r.register(IncBlockEntityTypes.RED_STRING_LIAR, RenderTileRedString::new);
		r.register(IncBlockEntityTypes.RED_STRING_CONSTRICTOR, RenderTileRedString::new);
		
		r.register(IncBlockEntityTypes.ENDER_SOUL_CORE, SoulCoreRenderers::createBlockEntityRenderer);
		r.register(IncBlockEntityTypes.POTION_SOUL_CORE, SoulCoreRenderers::createBlockEntityRenderer);
		
		r.register(IncBlockEntityTypes.SANVOCALIA_BIG, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.SANVOCALIA_SMALL, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.FUNNY_BIG, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.FUNNY_SMALL, RenderTileSpecialFlower::new);
		
		r.register(IncBlockEntityTypes.UNSTABLE_CUBE, UnstableCubeRenderers::createBlockEntityRenderer);
		
		r.register(IncBlockEntityTypes.DATA_FUNNEL, DataFunnelBlockEntityRenderer::new);
	}
	
	/// Color providers ///
	
	public static void registerBlockColorProviders(ColorHandler.BlockHandlerConsumer r) {
		//This does not inform block/item rendering, it uses a block entity renderer,
		//but it does tint the particles that show up when you break or run around on the blocks
		r.register((state, level, pos, tintIndex) -> {
			if(state.getBlock() instanceof UnstableCubeBlock cube) return cube.color.getFireworkColor();
			else return 0xFFFFFF;
		}, IncBlocks.UNSTABLE_CUBES.values().toArray(Block[]::new));
	}
	
	public static void registerItemColorProviders(ColorHandler.ItemHandlerConsumer r) {
		r.register((stack, tintIndex) -> IncItems.TICKET.get(stack).color(), IncItems.TICKET);
		r.register((stack, tintIndex) -> IncItems.TICKET_CONJURER.getType(stack).color(), IncItems.TICKET_CONJURER);
	}
	
	/// Item property overrides ///
	
	public static void registerPropertyOverrides(TriConsumer<ItemLike, ResourceLocation, ClampedItemPropertyFunction> r) {
		//Returns 1 if the corporea ticket has a request written on it, and 0 otherwise.
		//Now that I think about it, it's pretty much impossible to have a ticket with nothing written on it...
		r.accept(IncItems.TICKET, Inc.id("written_ticket"),
			(stack, level, ent, seed) -> !IncItems.TICKET.get(stack).isEmpty() ? 1 : 0);
	}
	
	/// Entity renderers ///
	
	public static void registerEntityRenderers(EntityRenderers.EntityRendererConsumer r) {
		r.accept(IncEntityTypes.FRACTURED_SPACE_COLLECTOR, NoopRenderer::new);
		r.accept(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, NoopRenderer::new);
	}
	
	/// Wand HUD capabilities ///
	
	public static final Map<BlockEntityType<?>, Function<BlockEntity, IWandHUD>> WAND_HUD_MAKERS = new HashMap<>();
	static {
		//Downcasts are required because, in Java, I can't express the typing relationship between the keys and values of the map.
		//I think it's like, a Map<BlockEntityType<? extends T>, Function<? super T, IWandHUD>>, but the T is scoped to the individual key/value pair, not the whole map.
		WAND_HUD_MAKERS.put(IncBlockEntityTypes.ENDER_SOUL_CORE, IncClientProperties::soulCoreHudDowncast);
		WAND_HUD_MAKERS.put(IncBlockEntityTypes.POTION_SOUL_CORE, IncClientProperties::soulCoreHudDowncast);
		
		WAND_HUD_MAKERS.put(IncBlockEntityTypes.SANVOCALIA_BIG, IncClientProperties::functionalFlowerHudDowncast);
		WAND_HUD_MAKERS.put(IncBlockEntityTypes.SANVOCALIA_SMALL, IncClientProperties::functionalFlowerHudDowncast);
		WAND_HUD_MAKERS.put(IncBlockEntityTypes.FUNNY_BIG, IncClientProperties::functionalFlowerHudDowncast);
		WAND_HUD_MAKERS.put(IncBlockEntityTypes.FUNNY_SMALL, IncClientProperties::functionalFlowerHudDowncast);
	}
	
	private static IWandHUD soulCoreHudDowncast(BlockEntity be) {
		return new AbstractSoulCoreBlockEntity.WandHud((AbstractSoulCoreBlockEntity) be);
	}
	
	private static IWandHUD functionalFlowerHudDowncast(BlockEntity be) {
		return new TileEntityFunctionalFlower.FunctionalWandHud<>((TileEntityFunctionalFlower) be);
	}
}
