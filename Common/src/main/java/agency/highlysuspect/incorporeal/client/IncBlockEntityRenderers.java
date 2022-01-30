package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import vazkii.botania.client.render.entity.EntityRenderers;
import vazkii.botania.client.render.tile.RenderTileRedString;

public class IncBlockEntityRenderers {
	public static void register(EntityRenderers.BERConsumer r) {
		r.register(IncBlockEntityTypes.RED_STRING_LIAR, RenderTileRedString::new);
	}
}
