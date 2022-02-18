package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModFluffBlocks;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import java.util.function.Consumer;

public class IncCommonRecipes {
	public static void addRecipes(Consumer<JsonFile> files) {
		//Corporea Solidifier
		RecipeDsl.shaped(IncBlocks.CORPOREA_SOLIDIFIER, "PPP", "EFE", "PPP")
			.define("P", Items.PAPER)
			.define("F", ModBlocks.corporeaFunnel)
			.define("E", ModTags.Items.INGOTS_ELEMENTIUM)
			.save(files);
		
		//Red Stringed Liar
		RecipeDsl.shaped(IncBlocks.RED_STRING_LIAR, "RRR", "RCS", "RRR")
			.define("R", ModBlocks.livingrock)
			.define("S", ModItems.redString)
			.define("C", ModBlocks.forestEye)
			.save(files);
		
		//Frame Tinkerer
		RecipeDsl.shaped(IncBlocks.FRAME_TINKERER, "SLS", "LFL")
			.define("S", ModTags.Items.INGOTS_MANASTEEL)
			.define("L", ModBlocks.livingrock)
			.define("F", Items.ITEM_FRAME)
			.save(files);
		
		//Corporea Retainer Evaporator
		RecipeDsl.shaped(IncBlocks.CORPOREA_RETAINER_EVAPORATOR, "P P", "PCP", "P P")
			.define("P", ModFluffBlocks.bluePavement)
			.define("C", ModItems.corporeaSpark)
			.save(files);
		
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
				.define("P", ModItems.getPetal(color))
				.define("E", Items.ENDER_PEARL)
				.save(files));
		
		//Soul Core Frame
		RecipeDsl.runic(IncItems.SOUL_CORE_FRAME, RunicAltarRecipeBuilder.TIER_3)
			//done in this funny way because runic altar recipes have an ingredient order -
			//it's cosmetic only, ofc, but i like this symmetrical ordering
			.add(Blocks.ICE, 4).add(ModItems.pixieDust).add(Blocks.ICE, 4).add(ModItems.pixieDust)
			.save(files);
		
		//Ender Soul Core
		RecipeDsl.runic(IncBlocks.ENDER_SOUL_CORE, RunicAltarRecipeBuilder.TIER_3 * 2)
			.add(IncItems.SOUL_CORE_FRAME)
			.add(ModTags.Items.GEMS_DRAGONSTONE, 2)
			.add(ModItems.manaweaveCloth, 2)
			.add(ModItems.enderHand)
			.save(files);
	}
}