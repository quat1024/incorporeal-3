package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.botania.common.lib.ModTags;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IncCommonDatagen {
	public static void configureCommonDatagen(DataGenerator gen) {
		List<Block> INC_BLOCKS = Registry.BLOCK.keySet().stream()
			.filter(rl -> rl.getNamespace().equals(Inc.MODID))
			.map(Registry.BLOCK::get)
			.collect(Collectors.toList());
		
		DataDsl.addProvider(gen, "Incorporeal block loot tables", files -> {
			//Blocks that don't drop themselves:
			Set<Block> selfDropExceptions = Set.of(IncBlocks.REDSTONE_ROOT_CROP);
			
			for(Block b : INC_BLOCKS) {
				if(selfDropExceptions.contains(b)) continue;
				DataDsl.saveBlockLootTable(files, b, DataDsl.selfDrop(b));
			}
		});
		
		DataDsl.addProvider(gen, "Incorporeal block and item tags", files -> {
			//corporea spark override
			DataDsl.blockTag(ModTags.Blocks.CORPOREA_SPARK_OVERRIDE).addBlocks(
				IncBlocks.RED_STRING_LIAR, IncBlocks.CLEARLY, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD
			).save(files);
			
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
		
		DataDsl.addProvider(gen, "Incorporeal recipes", IncCommonRecipes::addRecipes);
	}
}
