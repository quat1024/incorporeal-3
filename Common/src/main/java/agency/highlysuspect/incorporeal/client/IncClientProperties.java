package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.AbstractSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.entity.IncEntityTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.client.render.entity.EntityRenderers;
import vazkii.botania.client.render.tile.RenderTileRedString;
import vazkii.botania.client.render.tile.RenderTileSpecialFlower;
import vazkii.botania.client.render.tile.TEISR;
import vazkii.botania.common.block.tile.ModTiles;
import vazkii.botania.network.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IncClientProperties {
	//Botania has something like this too, in EntityRenderers, but it's not meant to be extended. So I will reimplement it myself.
	public static final Map<Block, Function<Block, TEISR>> BE_ITEM_RENDERER_FACTORIES = new HashMap<>();
	static {
		IncBlocks.UNSTABLE_CUBES.forEach((color, cube) -> BE_ITEM_RENDERER_FACTORIES.put(cube, TEISR::new));
	}
	
	/// Block render layers ///
	
	public static void registerRenderTypes(BiConsumer<Block, RenderType> r) {
		RenderType cutout = RenderType.cutout();
		
		r.accept(IncBlocks.NATURAL_REPEATER, cutout);
		r.accept(IncBlocks.NATURAL_COMPARATOR, cutout);
		r.accept(IncBlocks.REDSTONE_ROOT_CROP, cutout);
		
		r.accept(IncBlocks.SANVOCALIA, cutout);
		r.accept(IncBlocks.SANVOCALIA_SMALL, cutout);
		r.accept(IncBlocks.FLOATING_SANVOCALIA, cutout);
		r.accept(IncBlocks.FLOATING_SANVOCALIA_SMALL, cutout);
		
		r.accept(IncBlocks.FUNNY, cutout);
		r.accept(IncBlocks.FUNNY_SMALL, cutout);
		r.accept(IncBlocks.FLOATING_FUNNY, cutout);
		r.accept(IncBlocks.FLOATING_FUNNY_SMALL, cutout);
		
		for(Block b : IncBlocks.COMPRESSED_TATERS.values()) r.accept(b, cutout);
	}
	
	/// Block entity renderers ///
	
	public static void registerBlockEntityRenderers(EntityRenderers.BERConsumer r) {
		r.register(IncBlockEntityTypes.RED_STRING_LIAR, RenderTileRedString::new);
		
		r.register(IncBlockEntityTypes.SANVOCALIA_BIG, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.SANVOCALIA_SMALL, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.FUNNY_BIG, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.FUNNY_SMALL, RenderTileSpecialFlower::new);
		
		IncBlockEntityTypes.UNSTABLE_CUBES.forEach((color, type) ->
			r.register(type, context -> new UnstableCubeBlockEntityRenderer(color, context)));
	}
	
	/// Item property overrides ///
	
	public static void registerPropertyOverrides(TriConsumer<ItemLike, ResourceLocation, ClampedItemPropertyFunction> r) {
		//Returns 1 if the corporea ticket has a request written on it, and 0 otherwise.
		//Now that I think about it, it's pretty much impossible to have a ticket with nothing written on it...
		r.accept(IncItems.CORPOREA_TICKET, Inc.id("written_ticket"),
			(stack, level, ent, seed) -> IncItems.CORPOREA_TICKET.hasRequest(stack) ? 1 : 0);
	}
	
	/// Entity renderers ///
	
	public static void registerEntityRenderers(EntityRenderers.EntityRendererConsumer r) {
		r.accept(IncEntityTypes.FRACTURED_SPACE_COLLECTOR, NoopRenderer::new);
		r.accept(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, NoopRenderer::new);
	}
	
	/// Wand HUD capabilities ///
	
	public static void registerWandHudCaps(ModTiles.BECapConsumer<IWandHUD> r) {
		r.accept(be -> new AbstractSoulCoreBlockEntity.WandHud((AbstractSoulCoreBlockEntity) be),
			IncBlockEntityTypes.ENDER_SOUL_CORE);
		
		r.accept(be -> new TileEntityFunctionalFlower.FunctionalWandHud<>((TileEntityFunctionalFlower) be), 
			IncBlockEntityTypes.SANVOCALIA_BIG,
			IncBlockEntityTypes.SANVOCALIA_SMALL,
			IncBlockEntityTypes.FUNNY_BIG,
			IncBlockEntityTypes.FUNNY_SMALL);
	}
	
	/// Mojang's weird model-layer system ///
	
	public static void registerLayerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> r) {
		r.accept(ModelLayers.UNSTABLE_CUBE, () -> LayerDefinition.create(UnstableCubeModel.createMesh(), 32, 16));
	}
	
	public static class ModelLayers {
		public static final ModelLayerLocation UNSTABLE_CUBE = new ModelLayerLocation(Inc.id("unstable_cube"), "main");
	}
}
