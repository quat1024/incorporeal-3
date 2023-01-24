package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.util.CompressedTaterUtil;
import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.IncItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.function.Consumer;

public class IncCommonRecipeGen {
	public static void doIt(DataGenerator datagen, Consumer<JsonFile> files) {
		//Rod of the Fractured Space
		RecipeDsl.shaped(IncItems.FRACTURED_SPACE_ROD, " EG", " TE", "T  ")
			.define("E", Items.ENDER_EYE)
			.define("G", BotaniaItems.lifeEssence)
			.define("T", BotaniaItems.dreamwoodTwig)
			.save(files);
		
		//Corporea Solidifier
		RecipeDsl.shaped(IncBlocks.CORPOREA_SOLIDIFIER, "PPP", "EFE", "PPP")
			.define("P", Items.PAPER)
			.define("F", BotaniaBlocks.corporeaFunnel)
			.define("E", BotaniaTags.Items.INGOTS_ELEMENTIUM)
			.save(files);
		
		//Red Stringed Liar
		RecipeDsl.shaped(IncBlocks.RED_STRING_LIAR, "RRR", "RCS", "RRR")
			.define("R", BotaniaBlocks.livingrock)
			.define("S", BotaniaItems.redString)
			.define("C", BotaniaBlocks.forestEye)
			.save(files);
		
		//Red Stringed Constrictor
		RecipeDsl.shaped(IncBlocks.RED_STRING_CONSTRICTOR, "RRR", "RCS", "RRR")
			.define("R", BotaniaBlocks.livingrock)
			.define("S", BotaniaItems.redString)
			.define("C", BotaniaItems.dreamwoodTwig) //I never know which items to use for this stuff.
			.save(files);
		
		//Frame Tinkerer
		RecipeDsl.shaped(IncBlocks.FRAME_TINKERER, "LSL", "LFL")
			.define("S", BotaniaTags.Items.INGOTS_MANASTEEL)
			.define("L", BotaniaBlocks.livingwoodPlanks)
			.define("F", Items.ITEM_FRAME)
			.save(files);
		
		//Corporea Pylon
		RecipeDsl.shaped(IncBlocks.CORPOREA_PYLON, 4, " P ", " C ", "TCT")
			.define("P", BotaniaItems.corporeaSpark)
			.define("C", BotaniaBlocks.corporeaBlock)
			.define("T", BotaniaItems.terrasteel) //this mf expensive
			.save(files);
		
		//Bound Ender Pearl
		RecipeDsl.runic(IncItems.BOUND_ENDER_PEARL, RunicAltarRecipeBuilder.TIER_2)
			.add(BotaniaItems.manaPearl)
			.add(BotaniaItems.terrasteelNugget)
			.add(IncItems.ENTERBRILLIANCE)
			.save(files);
		
		//Soul Core Frame
		RecipeDsl.runic(IncItems.SOUL_CORE_FRAME, RunicAltarRecipeBuilder.TIER_3)
			//done in this funny way because runic altar recipes have an ingredient order -
			//it's cosmetic only, ofc, but i like this symmetrical ordering
			.add(Blocks.ICE, 4).add(BotaniaItems.pixieDust).add(Blocks.ICE, 4).add(BotaniaItems.pixieDust)
			.save(files);
		
		//Ender Soul Core
		RecipeDsl.runic(IncBlocks.ENDER_SOUL_CORE, RunicAltarRecipeBuilder.TIER_3 * 2)
			.add(IncItems.SOUL_CORE_FRAME)
			.add(BotaniaTags.Items.GEMS_DRAGONSTONE, 2)
			.add(BotaniaItems.manaweaveCloth, 2)
			.add(BotaniaItems.enderHand)
			.save(files);
		
		//Potion Soul Core
		RecipeDsl.runic(IncBlocks.POTION_SOUL_CORE, RunicAltarRecipeBuilder.TIER_3 * 2)
			.add(IncItems.SOUL_CORE_FRAME)
			.add(BotaniaTags.Items.GEMS_DRAGONSTONE, 2)
			.add(BotaniaItems.manaweaveCloth, 2)
			.add(BotaniaItems.bloodPendant)
			.save(files);
		
		//Stonecutting for the Natural Devices so you dont have to get them randomly lol
		RecipeDsl.stonecutting(IncBlocks.NATURAL_REPEATER).input(BotaniaItems.redstoneRoot).save(files);
		RecipeDsl.stonecutting(IncBlocks.NATURAL_COMPARATOR).input(BotaniaItems.redstoneRoot).save(files);
		
		//Sanvocalia
		RecipeDsl.apothecary(IncBlocks.SANVOCALIA)
			.addPetals(DyeColor.WHITE, DyeColor.ORANGE, DyeColor.ORANGE, DyeColor.RED)
			.add(BotaniaItems.runeLust)
			.add(BotaniaItems.pixieDust)
			.add(BotaniaItems.redstoneRoot)
			.save(files);
		
		//Funny
		RecipeDsl.apothecary(IncBlocks.FUNNY)
			.addPetals(DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.PURPLE)
			.add(BotaniaItems.redstoneRoot)
			.save(files);
		
		//X -> Mini X
		RecipeDsl.miniFlower(IncBlocks.SANVOCALIA, IncBlocks.SANVOCALIA_SMALL).save(files);
		RecipeDsl.miniFlower(IncBlocks.FUNNY, IncBlocks.FUNNY_SMALL).save(files);
		
		//X -> Floating X
		RecipeDsl.floatingFlower(IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA).save(files);
		RecipeDsl.floatingFlower(IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA_SMALL).save(files);
		RecipeDsl.floatingFlower(IncBlocks.FUNNY, IncBlocks.FLOATING_FUNNY).save(files);
		RecipeDsl.floatingFlower(IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY_SMALL).save(files);
		
		//Unstable Cubes
		IncBlocks.UNSTABLE_CUBES.forEach((color, cube) ->
			RecipeDsl.shaped(cube, 4, "OPO", "PEP", "OPO")
				.group("unstable_cubes")
				.define("O", Blocks.OBSIDIAN)
				.define("P", BotaniaItems.getPetal(color))
				.define("E", Items.ENDER_PEARL)
				.save(files));
		//eh fuck it why not
		RecipeDsl.stonecuttingGroup(IncBlocks.UNSTABLE_CUBES.values())
			.group("unstable_cubes")
			.save(files);
		
		//Petal block carpets
		IncBlocks.PETAL_CARPETS.forEach((color, carpet) ->
			RecipeDsl.shaped(carpet, 3, "XX")
				.group("petal_carpets")
				.define("X", BotaniaBlocks.getPetalBlock(color))
				.save(files));
		
		//Compressed Tiny Potatoes
		for(int i = CompressedTaterUtil.SMALLEST; i < CompressedTaterUtil.LARGEST; i++) {
			Block small = CompressedTaterUtil.getPotato(i);
			Block big = CompressedTaterUtil.getPotato(i + 1);
			
			ResourceLocation compressId = Inc.id("tater/compress_" + i);
			ResourceLocation uncompressId = Inc.id("tater/uncompress_" + i);
			
			RecipeDsl.compress9(small, big).save(files, compressId);
			RecipeDsl.uncompressTo9(big, small).save(files, uncompressId);
		}
	}
}
