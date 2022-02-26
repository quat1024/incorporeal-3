package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModFluffBlocks;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data generators for things that are the same across both mod loaders.
 * 
 * @see IncFabricDatagen for the entrypoint; fabric hosts the common-side datagen.
 */
@SuppressWarnings("JavadocReference") //incfabricdatagen
public class IncCommonDatagen {
	public static void configureCommonDatagen(DataGenerator gen) {
		DataDsl.addProvider(gen, "Incorporeal block loot tables", (datagen, files) -> {
			List<Block> INC_BLOCKS = Registry.BLOCK.keySet().stream()
				.filter(rl -> rl.getNamespace().equals(Inc.MODID))
				.map(Registry.BLOCK::get)
				.collect(Collectors.toList());
			
			//Blocks that don't drop themselves:
			Set<Block> selfDropExceptions = Set.of(IncBlocks.REDSTONE_ROOT_CROP);
			
			for(Block b : INC_BLOCKS) {
				if(selfDropExceptions.contains(b)) continue;
				DataDsl.saveBlockLootTable(files, b, DataDsl.selfDrop(b));
			}
		});
		
		DataDsl.addProvider(gen, "Incorporeal block and item tags", (datagen, files) -> {
			//rods
			DataDsl.itemTag(ModTags.Items.RODS)
				.addItems(IncItems.FRACTURED_SPACE_ROD)
				.save(files);
			
			//corporea spark override
			DataDsl.blockTag(ModTags.Blocks.CORPOREA_SPARK_OVERRIDE)
				.addBlocks(IncBlocks.RED_STRING_LIAR, IncBlocks.CLEARLY, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD)
				.save(files);
			
			//functional special flowers
			DataDsl.blockAndItemTag(ModTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS).add(
				IncBlocks.FUNNY, IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.SANVOCALIA, IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA_SMALL
			).save(files);
			
			//mini flowers
			DataDsl.blockAndItemTag(ModTags.Blocks.MINI_FLOWERS).add(
				IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA_SMALL
			).save(files);
			
			//special floating flowers
			DataDsl.blockAndItemTag(ModTags.Blocks.SPECIAL_FLOATING_FLOWERS).add(
				IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.FLOATING_SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA_SMALL
			).save(files);
		});
		
		DataDsl.addProvider(gen, "Incorporeal recipes", (datagen, files) -> {
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
		});
		
		DataDsl.addProvider(gen, "Incorporeal Lexica Botania entries", IncLexiconDatagen::doIt);
	}
}
