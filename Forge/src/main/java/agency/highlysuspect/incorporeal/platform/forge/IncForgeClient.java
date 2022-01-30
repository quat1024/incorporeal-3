package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.client.IncItemProperties;
import net.minecraft.client.renderer.item.ItemProperties;

public class IncForgeClient {
	public static void init() {
		IncItemProperties.register((item, id, prop) -> ItemProperties.register(item.asItem(), id, prop));
	}
}
