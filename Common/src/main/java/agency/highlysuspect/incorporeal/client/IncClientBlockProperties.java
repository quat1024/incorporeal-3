package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import vazkii.botania.client.render.entity.EntityRenderers;
import vazkii.botania.client.render.tile.RenderTileRedString;
import vazkii.botania.client.render.tile.RenderTileSpecialFlower;

import java.util.function.BiConsumer;

public class IncClientBlockProperties {
	public static void registerRenderTypes(BiConsumer<Block, RenderType> r) {
		r.accept(IncBlocks.SANVOCALIA, RenderType.cutout());
		r.accept(IncBlocks.SANVOCALIA_SMALL, RenderType.cutout());
		r.accept(IncBlocks.FLOATING_SANVOCALIA, RenderType.cutout());
		r.accept(IncBlocks.FLOATING_SANVOCALIA_SMALL, RenderType.cutout());
		
		r.accept(IncBlocks.FUNNY, RenderType.cutout());
		r.accept(IncBlocks.FUNNY_SMALL, RenderType.cutout());
		r.accept(IncBlocks.FLOATING_FUNNY, RenderType.cutout());
		r.accept(IncBlocks.FLOATING_FUNNY_SMALL, RenderType.cutout());
	}
	
	public static void registerBlockEntityRenderers(EntityRenderers.BERConsumer r) {
		r.register(IncBlockEntityTypes.RED_STRING_LIAR, RenderTileRedString::new);
		
		r.register(IncBlockEntityTypes.SANVOCALIA_BIG, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.SANVOCALIA_SMALL, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.FUNNY_BIG, RenderTileSpecialFlower::new);
		r.register(IncBlockEntityTypes.FUNNY_SMALL, RenderTileSpecialFlower::new);
		
		IncBlockEntityTypes.UNSTABLE_CUBES.forEach((color, type) ->
			r.register(type, context -> new UnstableCubeRenderer(color, context)));
	}
}