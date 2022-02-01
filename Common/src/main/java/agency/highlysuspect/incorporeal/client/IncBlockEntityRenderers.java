package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.client.model.UnstableCubeRenderer;
import vazkii.botania.client.render.entity.EntityRenderers;
import vazkii.botania.client.render.tile.RenderTileRedString;

public class IncBlockEntityRenderers {
	public static void register(EntityRenderers.BERConsumer r) {
		r.register(IncBlockEntityTypes.RED_STRING_LIAR, RenderTileRedString::new);
		
		IncBlockEntityTypes.UNSTABLE_CUBES.forEach((color, type) ->
			r.register(type, context -> new UnstableCubeRenderer(color, context)));
	}
}
