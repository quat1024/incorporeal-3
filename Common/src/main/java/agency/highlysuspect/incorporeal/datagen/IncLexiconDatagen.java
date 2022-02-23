package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import vazkii.botania.common.item.ModItems;

import java.util.function.Consumer;

public class IncLexiconDatagen {
	public static final ResourceLocation BOOK_ID = Inc.id("lexicon");
	public static final int INCORPOREAL_BLUE = 0x194a5c;
	
	private static PatchouliEntryBuilder builder(String path) {
		return new PatchouliEntryBuilder(BOOK_ID, path).color(INCORPOREAL_BLUE);
	}
	
	public static void doIt(DataGenerator generator, Consumer<JsonFile> files) {
		//Devices
		PatchouliEntryBuilder frameTinkerer = builder("devices/frame_tinkerer")
			.nameAndIcon(IncBlocks.FRAME_TINKERER)
			.category("botania:devices")
			.text("The $(item)Frame Tinkerer$(0) is an indispensible tool for any advanced corporeticist, or really anyone who likes to redecorate often. It acts like a $(item)Spark Tinkerer$(0) - drop an item on it, give it a redstone signal, and the item will change places with one in a random nearby $(item)Item Frame$(0).")
			.text("Note that it doesn't have a dedicated inventory; just dropping items on top will suffice. They are pushable via $(item)Piston$(0).")
			.crafting(IncBlocks.FRAME_TINKERER, "Symbiotic Hematophage")
			.save(generator, files);
		
		//Ender
		
		//Challenges
		builder("challenges/corporea_sorting_hall")
			.challenge()
			.name("Corporea Sorting Hall")
			.icon(ModItems.corporeaSparkMaster)
			.checkboxQuest("Corporea Sorting Hall", "Create a $(thing)Corporea Network$(0) that doesn't simply store items out-of-sight, but organizes them into logical groups so you can manually access each item too, instead of relying on a $(item)Corporea Index$(0). Bonus points if it can unpack and sort $(item)Shulker Boxes$(0) too.")
			.save(generator, files);
		
		builder("challenges/pixel_screen")
			.challenge()
			.icon(IncItems.FRAME_TINKERER)
			.checkboxQuest("Pixel Screen", "Repurpose the $(item)Frame Tinkerer$(0) to create a dynamic, changing map-art display on a wall, floor, or ceiling covered in $(item)Glow Item Frames$(0). Bonus points if it actually displays something useful...")
			.relations0(frameTinkerer)
			.save(generator, files);
		
		
	}
}
