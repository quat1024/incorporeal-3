package agency.highlysuspect.incorporeal.platform.fabric.datagen;

import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.datagen.DataDsl;
import agency.highlysuspect.incorporeal.datagen.JsonFile;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public class IncFabricTagGen {
	public static void doIt(DataGenerator datagen, Consumer<JsonFile> files) {
		DataDsl.itemTag(accessory("head/face"))
			.addItems(IncItems.DATA_MONOCLE)
			.save(files);
	}
	
	private static TagKey<Item> accessory(String name) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("trinkets", name));
	}
}
