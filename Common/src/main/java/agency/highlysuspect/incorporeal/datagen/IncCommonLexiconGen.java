package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.IncItems;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import vazkii.botania.common.item.ModItems;

import java.util.function.Consumer;

public class IncCommonLexiconGen {
	public static final ResourceLocation BOOK_ID = Inc.id("lexicon");
	public static final int INCORPOREAL_BLUE = 0x04759e;
	
	private static PatchouliEntryBuilder builder(String path) {
		return new PatchouliEntryBuilder(BOOK_ID, path).color(INCORPOREAL_BLUE);
	}
	
	public static void doIt(DataGenerator generator, Consumer<JsonFile> files) {
		//Book.json
		JsonObject lol = new JsonObject();
		lol.addProperty("extend", "botania:lexicon");
		files.accept(JsonFile.create(lol, "data", BOOK_ID.getNamespace(), "patchouli_books", BOOK_ID.getPath(), "book"));
		
		//Devices
		PatchouliEntryBuilder frameTinkerer = builder("devices/frame_tinkerer")
			.nameAndIcon(IncBlocks.FRAME_TINKERER)
			.devicesCategory()
			.text("When the $(item)Frame Tinkerer$(0) gets a $(thing)redstone signal$(0), it'll randomly pick a nearby $(item)Item Frame$(0) located in an adjacent block space, and switch the item displayed in the $(item)Frame$(0) with an item dropped on top of the $(item)Frame Tinkerer$(0). It doesn't have a dedicated inventory; just dropping items on top will suffice. They are pushable via $(item)Piston$(0).")
			.crafting(IncBlocks.FRAME_TINKERER, "Symbiotic Hematophage")
			.text("Useful for very advanced $(thing)Corporea Networks$(0), or if you just like to redecorate often.")
			.relations0("botania:mana/spark_changer")
			.save(generator, files);
		
		//Functional Flowers
		PatchouliEntryBuilder funny = builder("functional_flowers/funny")
			.nameAndIcon(IncBlocks.FUNNY)
			.functionalFlowersCategory()
			.text("Legend has it that placing $(item)Note Blocks$(0) around a mana-fed $(item)Sweet Alexum$(0) flower will cause the note blocks to resonate with only the most beautiful of frequencies. For best results, four $(item)Note Blocks$(0) are required to play all of the parts: one above $(item)sand$(0), one above $(item)wood$(0), one above $(item)rock$(0), and one above a block composed of the $(item)purest balance$(0).")
			.text("They also say providing the flower with a high-powered $(thing)Redstone Signal$(0) will restart the tune, while a low-powered signal will merely pause it, and that the $(item)Sweet Alexum Petite$(0) needs the $(item)Note Blocks$(0) to be placed closer but plays the song at a faster clip.<br><br>I wonder if these legends have any truth to them?")
			.petalApothecary(IncBlocks.FUNNY, "The other modders make me dance to the funny flower and shout go little dragon go")
			.extraRecipeMapping(IncBlocks.FLOATING_FUNNY)
			.extraRecipeMapping(IncBlocks.FUNNY_SMALL)
			.extraRecipeMapping(IncBlocks.FLOATING_FUNNY_SMALL)
			.save(generator, files);
		
		PatchouliEntryBuilder sanvocalia = builder("functional_flowers/sanvocalia")
			.nameAndIcon(IncBlocks.SANVOCALIA)
			.functionalFlowersCategory()
			.elven()
			.text("The $(item)Sanvocalia$(0) is a flower especially attuned to corporetic frequencies. When this flower has $(item)Mana$(0) available and is near a $(item)Corporea Index$(0), it will whisper the requests written on nearby $(item)Corporea Tickets$(0) to the $(item)Index$(0).<br><br>Tickets can be created with a $(item)Corporea Solidifier$(0) or a $(item)Ticker Conjurer$(0).")
			.petalApothecary(IncBlocks.SANVOCALIA, "Do you still remember?")
			.relations0(
				"incorporeal:ender/corporea_solidifier", //circular reference xd
				"incorporeal:ender/ticket_conjurer"
			)
			.extraRecipeMapping(IncBlocks.FLOATING_SANVOCALIA)
			.extraRecipeMapping(IncBlocks.SANVOCALIA_SMALL)
			.extraRecipeMapping(IncBlocks.FLOATING_SANVOCALIA_SMALL)
			.save(generator, files);
		
		//Ender
		PatchouliEntryBuilder corporeaSolidifier = builder("ender/corporea_solidifier")
			.nameAndIcon(IncBlocks.CORPOREA_SOLIDIFIER)
			.enderCategory()
			.elven()
			.text("The $(item)Corporea Solidifier$(0) acts as a replacement for the $(item)Corporea Retainer$(0). When placed next to a $(item)Corporea Interceptor$(0), the $(item)Solidifier$(0), instead of remembering the interceptor's request, will conjure up a $(thing)Corporea Ticket$(0) item, encapsulating the request's item and size.")
			.text("The $(thing)Ticket$(0) will be pushed into an inventory placed a block or two below the $(item)Solidifier$(0), or just dropped into the world above the $(item)Solidifier$(0) if none are available, much like the behavior of the $(item)Corporea Funnel$(0).")
			.crafting(IncBlocks.CORPOREA_SOLIDIFIER, "Up, up, down, up")
			.text("The $(item)Sanvocalia$(0) functional flower seems to be particularly interested in the $(thing)Corporea Ticket$(0)'s energies.")
			.relations0(
				sanvocalia,
				"incorporeal:ender/ticket_conjurer", //forward reference
				"botania:ender/corporea_interceptor",
				"botania:ender/corporea_retainer",
				"botania:ender/corporea_funnel"
			)
			.save(generator, files);
		
		PatchouliEntryBuilder redStringLiar = builder("ender/red_string_liar")
			.nameAndIcon(IncBlocks.RED_STRING_LIAR)
			.enderCategory()
			.elven()
			.text("The $(item)Red Stringed Liar$(0) works best on a $(thing)Corporea Network$(0). It will bind to any block with an inventory, much like the $(item)Red Stringed Container$(0), and tricks the $(thing)Corporea Network$(0) into thinking the inventory contains the items located in any $(item)Item Frames$(0) placed against the $(item)Liar$(0), instead of whatever items are in the actual inventory.")
			.text("For example, if a $(item)Red Stringed Liar$(0) is bound to an inventory containing 10 $(item)cookies$(0), and two $(item)Item Frames$(0) showing $(thing)cake$(0) and $(thing)apples$(0) are attached to the $(item)Liar$(0), it will report 10 $(thing)cakes$(0), 10 $(thing)apples$(0), and zero $(thing)cookies$(0) to the $(thing)Corporea Network$(0). If $(thing)cake$(0) or $(thing)apples$(0) are then requested through corporea, it will retrieve $(item)cookies$(0).")
			.text("Extracting items through the $(item)Red Stringed Liar$(0) retrieves the real items from the bound inventory, so what is asked for may not match what is actually received. (This doesn't duplicate any items!)")
			.text("Since $(item)Corporea Funnels$(0) can only request $(italic)specific$() items, instead of \"whatever\": one possibility for this block is to allow a $(item)Funnel$(0) to extract $(italic)everything$() from a given inventory, regardless of what's actually inside. The requested item would instead act as a label for which inventory to pull from.<br><br>It's a little bit hard to explain - just experiment with it.")
			.crafting(IncBlocks.RED_STRING_LIAR, "You Would Not Believe This Random Hecking Nonsense")
			.relations0("botania:ender/red_string")
			.save(generator, files);
		
		PatchouliEntryBuilder redStringConstrictor = builder("ender/red_string_constrictor")
			.nameAndIcon(IncBlocks.RED_STRING_CONSTRICTOR)
			.enderCategory()
			.elven()
			.text("When unpowered, the $(item)Red Stringed Constrictor$(0) acts the same as a $(item)Red Stringed Container$(0). But giving it a $(thing)redstone signal$(0) will cause it to expose fewer slots of the targeted inventory - a signal strength of 1 will expose everything but the first slot, 2 will skip the first two, and so on.")
			.crafting(IncBlocks.RED_STRING_CONSTRICTOR, "It feels like we only go backwards")
			.text("A $(thing)right click$(0) will toggle whether it removes slots from the end of the inventory, instead of from the front.<br><br>Naturally, it only binds to inventories that have a sense of \"slots\" - this may depend on certain $(thing)metaphysical properties of your world$(0). $(7)And by that nonsense I mean it works slightly differently on Fabric and Forge. Sorry about that.$(0)")
			.relations0("botania:ender/red_string")
			.save(generator, files);
		
		PatchouliEntryBuilder soulCores = builder("ender/soul_cores")
			.name("Soul Cores")
			.icon(IncItems.SOUL_CORE_FRAME)
			.enderCategory()
			.elven()
			.text("$(item)Soul Cores$(0) surround and envelop a $(thing)Player's Soul$(0) in a light $(thing)Mana$(0) solution, ever-so-slightly reanimating it. While the extracted $(thing)Soul$(0) isn't capable of too much independent thought, as long as the the real, live $(thing)Player$(0) is present in the $(thing)Dimension$(0), they act somewhat entangled.")
			.text("To inform a $(item)Soul Core$(0) of your presence, right click it. This process has been known to sting a bit.<br><br>If a $(item)Soul Core$(0) ever runs out of mana, it will violently close the link to the $(thing)Soul$(0) it's bound to; right clicking again will re-establish it.")
			
			.text("All $(item)Soul Cores$(0) are built out of the $(item)Soul Core Frame$(0):")
			.runicAltar(IncItems.SOUL_CORE_FRAME, "Show me your worth, Mortals")
			
			.text("The $(item)Ender Soul Core$(0) provides block-level access to the $(item)Ender Chest$(0) inventory of the bound $(thing)Player$(0). As long as the $(thing)Player$(0) is present in the dimension, the $(item)Ender Soul Core's$(0) inventory will mirror their $(item)Ender Chest's$(0).<br><br>The usual interactions work; hoppers, corporea sparks, comparators... Measuring is free, but adding and removing items costs $(thing)Mana$(0).")
			.runicAltar(IncBlocks.ENDER_SOUL_CORE, "Driven Drop")
			
			.text("The $(item)Blood Soul Core$(0), at the cost of $(thing)Mana$(0), injects all potion effects received via $(item)Splash$(0) or $(item)Lingering Potions$(0) into the bloodstream of the bound $(thing)Player$(0). Its $(thing)Comparator$(0) signal reflects the bound $(thing)Player$(0)'s health.<br><br>Note that since the $(thing)Soul$(0) suspended in a $(item)Soul Core$(0) is technically undead, certain potion effects such as $(item)Instant Health$(0) may not have the desired effect.")
			.runicAltar(IncBlocks.POTION_SOUL_CORE, "A heart-to-heart")
		
			.relations("Corporea Soul Core?", "This block was removed in 1.18, but was replaced with a simpler method to do the same thing.", "incorporeal:misc/corporea_player_heads")
			.save(generator, files);
		
		PatchouliEntryBuilder ticketConjurer = builder("ender/ticket_conjurer")
			.nameAndIcon(IncItems.SOLIDIFIED_REQUEST_CONJURER)
			.enderCategory()
			.elven()
			.text("The $(item)Ticket Conjurer$(0) allows one to conveniently create $(item)Corporea Tickets$(0). Simply hold it in either hand, speak a $(thing)Corporea Request$(0) as you would when using a $(item)Corporea Index$(0), and a $(item)Ticket$(0) corresponding to the request will be left in your inventory.<br><br>The word \"this\" will refer to the item in your left hand when the Conjurer is held in your right, and vice versa.")
			.crafting(IncItems.SOLIDIFIED_REQUEST_CONJURER, "Dying Breath of Stokesia")
			.text("Advanced corporeticists should know: right clicking it will cycle through different types of Tickets it is possible to create. $(thing)Corporea$(0) tickets are the default type, encapsulating a \"corporea matcher\" and \"count\" pair, but there are also tickets for $(thing)matchers$(0) and $(thing)numbers$(0) individually, as well as a ticket representing $(thing)nothing$(0).")
			.relations0(
				"botania:ender/corporea_index",
				corporeaSolidifier,
				sanvocalia
			)
			.save(generator, files);
		
		//Misc
		builder("misc/corporea_player_heads")
			.name("Corporea Access Control")
			.miscCategory()
			.icon(Blocks.PLAYER_HEAD)
			.elven()
			.text("With Incorporeal installed, you can place $(thing)Corporea Sparks$(0) onto $(thing)Player Heads$(0). This acts as a simple security system for a $(thing)Corporea Network$(0) - any $(thing)Players$(0) who do not have their head on the network cannot use any $(item)Corporea Indices$(0).")
			.relations(null, "Now let me see you $(2)DANCE$(0)", 
				"botania:ender/corporea_index",
				"botania:misc/head_creating"
			)
			.text("$(7)This supplants the \"Corporea Soul Core\" block from previous versions - I figured the mechanic should be cheaper.$(0)")
			.save(generator, files);
		
		builder("misc/unstable_cubes")
			.name("Unstable Cubes")
			.miscCategory()
			.icon(IncBlocks.UNSTABLE_CUBES.get(DyeColor.RED))
			.text("$(item)Unstable Cubes$(0) are curious little critters. They crackle pleasantly and spin in a satisfying way when touched, but other than emitting a $(thing)redstone signal$(0) when that happens, don't seem to do anything useful.")
			.craftingMulti(IncBlocks.UNSTABLE_CUBES.values(), "Decorative relic from a bygone era")
			.save(generator, files);
		
		//Tools
		PatchouliEntryBuilder fracturedSpaceRod = builder("tools/fractured_space_rod")
			.nameAndIcon(IncItems.FRACTURED_SPACE_ROD)
			.toolsCategory()
			.elven()
			.text("The $(item)Rod of the Fractured Space$(0) has the unique ability to, albeit briefly, open a small wormhole between two points in space. Only $(item)Items$(0) seem to be small and light enough to fit.$(br)Right click on an $(item)Open Crate$(0) to set the destination location.")
			.text("Then, any time you right click on the ground, all $(item)Items$(0) nearby will be sucked in to the wormhole and dropped out of the $(item)Open Crate$(0), no matter where they are. The process consumes more $(item)Mana$(0) when more items are sent at once; perhaps packaging them up into something like a $(item)Shulker Box$(0) first would be beneficial.")
			.crafting(IncItems.FRACTURED_SPACE_ROD, "- thread of fate manipulator -")
			.save(generator, files);
		
		//Challenges
		builder("challenges/corporea_sorting_hall")
			.icon(ModItems.corporeaSparkMaster)
			.challenge("Corporea Sorting Hall", "Create a $(thing)Corporea Network$(0) that doesn't simply store items out-of-sight, but organizes them into logical groups so you can manually access each item too, instead of relying on a $(item)Corporea Index$(0). Bonus points if it can unpack and sort $(item)Shulker Boxes$(0) too.")
			.save(generator, files);

		builder("challenges/pixel_screen")
			.icon(IncItems.FRAME_TINKERER)
			.challenge("Pixel Screen", "Repurpose the $(item)Frame Tinkerer$(0) to create a dynamic, changing map-art display on a wall, floor, or ceiling covered in $(item)Glow Item Frames$(0). Bonus points if it actually displays something useful...")
			.relations0(frameTinkerer)
			.save(generator, files);
		
		builder("challenges/remote_corporea")
			.icon(ModItems.corporeaSpark)
			.challenge("Remote Corporea", "Create a $(thing)Corporea Network$(0) capable of performing requests on your behalf, and delivering the items to you from anywhere in the same dimension. Bonus points if it works in at least one other dimension, and even more bonus points if it delivers information about failed requests too.")
			.relations0(
				"botania:ender/corporea",
				"botania:ender/corporea_index",
				"botania:ender/ender_hand",
				soulCores,
				fracturedSpaceRod,
				ticketConjurer,
				sanvocalia
			)
			.save(generator, files);
		
		builder("challenges/wireless_redstone")
			.icon(Items.REDSTONE)
			.challenge("Wireless Redstone", "Figure out some way to create short-range $(thing)Wireless Redstone$(0). It's possible!")
			.save(generator, files);
	}
}
