package agency.highlysuspect.incorporeal;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class IncTags {
	public static class Items {
		public static final TagKey<Item> DATA_VIEWERS = TagKey.create(Registry.ITEM_REGISTRY, Inc.id("data_viewers"));
	}
}
