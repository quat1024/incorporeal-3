package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.entity.IncEntityTypes;
import net.minecraft.client.renderer.entity.NoopRenderer;
import vazkii.botania.client.render.entity.EntityRenderers;

public class IncClientEntityProperties {
	public static void registerEntityRenderers(EntityRenderers.EntityRendererConsumer r) {
		r.accept(IncEntityTypes.FRACTURED_SPACE_COLLECTOR, NoopRenderer::new);
	}
}
