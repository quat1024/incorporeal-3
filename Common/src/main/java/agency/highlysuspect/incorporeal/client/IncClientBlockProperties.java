package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.block.AbstractSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.IncBlockEntityTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.client.render.entity.EntityRenderers;
import vazkii.botania.client.render.tile.RenderTileRedString;
import vazkii.botania.client.render.tile.RenderTileSpecialFlower;
import vazkii.botania.common.block.tile.ModTiles;

import java.util.function.BiConsumer;

public class IncClientBlockProperties {
	public static void registerRenderTypes(BiConsumer<Block, RenderType> r) {
		RenderType cutout = RenderType.cutout();
		
		r.accept(IncBlocks.NATURAL_REPEATER, cutout);
		r.accept(IncBlocks.NATURAL_COMPARATOR, cutout);
		
		r.accept(IncBlocks.SANVOCALIA, cutout);
		r.accept(IncBlocks.SANVOCALIA_SMALL, cutout);
		r.accept(IncBlocks.FLOATING_SANVOCALIA, cutout);
		r.accept(IncBlocks.FLOATING_SANVOCALIA_SMALL, cutout);
		
		r.accept(IncBlocks.FUNNY, cutout);
		r.accept(IncBlocks.FUNNY_SMALL, cutout);
		r.accept(IncBlocks.FLOATING_FUNNY, cutout);
		r.accept(IncBlocks.FLOATING_FUNNY_SMALL, cutout);
	}
	
	public static void registerBlockEntityRenderers(EntityRenderers.BERConsumer r) {
		r.register(IncBlockEntityTypes.RED_STRING_LIAR, RenderTileRedString::new);
		
		r.register(IncBlockEntityTypes.SANVOCALIA_BIG, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.SANVOCALIA_SMALL, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.FUNNY_BIG, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.FUNNY_SMALL, RenderTileSpecialFlower::new);
		
		IncBlockEntityTypes.UNSTABLE_CUBES.forEach((color, type) ->
			r.register(type, context -> new UnstableCubeBlockEntityRenderer(color, context)));
	}
	
	public static void registerWandHudCaps(ModTiles.BECapConsumer<IWandHUD> r) {
		r.accept(be -> new AbstractSoulCoreBlockEntity.WandHud((AbstractSoulCoreBlockEntity) be),
			IncBlockEntityTypes.ENDER_SOUL_CORE);
		
		r.accept(be -> new TileEntityFunctionalFlower.FunctionalWandHud<>((TileEntityFunctionalFlower) be), 
			IncBlockEntityTypes.SANVOCALIA_BIG,
			IncBlockEntityTypes.SANVOCALIA_SMALL,
			IncBlockEntityTypes.FUNNY_BIG,
			IncBlockEntityTypes.FUNNY_SMALL);
	}
}