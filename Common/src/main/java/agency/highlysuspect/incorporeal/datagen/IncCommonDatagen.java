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

import static agency.highlysuspect.incorporeal.datagen.DataDsl.addProvider;
import static agency.highlysuspect.incorporeal.datagen.DataDsl.blockAndItem;
import static agency.highlysuspect.incorporeal.datagen.DataDsl.blockTag;
import static agency.highlysuspect.incorporeal.datagen.DataDsl.selfDrop;

public class IncCommonDatagen {
	public static void configureCommonDatagen(DataGenerator gen) {
		List<Block> INC_BLOCKS = Registry.BLOCK.keySet().stream()
			.filter(rl -> rl.getNamespace().equals(Inc.MODID))
			.map(Registry.BLOCK::get)
			.collect(Collectors.toList());
		
		addProvider(gen, "Incorporeal block loot tables", files -> {
			//Blocks that don't drop themselves
			Set<Block> selfDropExceptions = Set.of(IncBlocks.REDSTONE_ROOT_CROP);
			
			for(Block b : INC_BLOCKS) {
				if(selfDropExceptions.contains(b)) continue;
				files.accept(selfDrop(b));
			}
		});
		
		addProvider(gen, "Incorporeal block and item tags", files -> {
			files.accept(blockTag(ModTags.Blocks.CORPOREA_SPARK_OVERRIDE,
				IncBlocks.RED_STRING_LIAR, IncBlocks.CLEARLY, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD));
			
			//todo: maybe this abstraction needs some work lol
			blockAndItem(files, ModTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS, ModTags.Items.FUNCTIONAL_SPECIAL_FLOWERS,
				IncBlocks.FUNNY, IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.SANVOCALIA, IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA_SMALL);
			
			blockAndItem(files, ModTags.Blocks.MINI_FLOWERS, ModTags.Items.MINI_FLOWERS,
				IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA_SMALL);
			
			blockAndItem(files, ModTags.Blocks.SPECIAL_FLOATING_FLOWERS, ModTags.Items.SPECIAL_FLOATING_FLOWERS,
				IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.FLOATING_SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA_SMALL);
		});
		
		addProvider(gen, "Incorporeal recipes", IncCommonRecipes::addRecipes);
	}
}
