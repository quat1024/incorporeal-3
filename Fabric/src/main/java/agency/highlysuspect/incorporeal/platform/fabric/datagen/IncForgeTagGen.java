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

/**
 * Uhh, it would probably make sense for this to be under the Forge source set...
 * However, I don't have any Forge datagen scaffolding set up at the moment
 * all of the datagen tasks are ran through fabric-loader.
 */
public class IncForgeTagGen {
	public static void doIt(DataGenerator datagen, Consumer<JsonFile> files) {
		DataDsl.itemTag(accessory("charm"))
			.addItems(IncItems.DATA_MONOCLE)
			.save(files);
	}
	
	private static TagKey<Item> accessory(String name) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("curios", name));
	}
}
