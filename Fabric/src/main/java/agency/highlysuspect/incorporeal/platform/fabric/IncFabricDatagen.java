package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.botania.common.lib.ModTags;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static agency.highlysuspect.incorporeal.datagen.DataDsl.*;

public class IncFabricDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		System.out.println("datagen output folder: " + gen.getOutputFolder().toAbsolutePath());
		configureCommonDatagen(gen);
	}
	
	private static void configureCommonDatagen(DataGenerator gen) {
		List<Block> INC_BLOCKS = Registry.BLOCK.keySet().stream()
			.filter(rl -> rl.getNamespace().equals(Inc.MODID))
			.map(Registry.BLOCK::get)
			.collect(Collectors.toList());
		
		//TODO: Code should probably be moved under the common sourceset
		addProvider(gen, "Incorporeal block loot tables", pit -> {
			//Blocks that don't drop themselves
			Set<Block> selfDropExceptions = Set.of(IncBlocks.REDSTONE_ROOT_CROP);
			
			for(Block b : INC_BLOCKS) {
				if(selfDropExceptions.contains(b)) continue;
				pit.accept(selfDrop(b));
			}
		});
		
		addProvider(gen, "Incorporeal block and item tags", pit -> {
			pit.accept(blockTag(ModTags.Blocks.CORPOREA_SPARK_OVERRIDE,
				IncBlocks.RED_STRING_LIAR, IncBlocks.CLEARLY, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD));
			
			//todo: maybe this abstraction needs some work lol
			blockAndItem(pit, ModTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS, ModTags.Items.FUNCTIONAL_SPECIAL_FLOWERS,
				IncBlocks.FUNNY, IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.SANVOCALIA, IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA_SMALL);
			
			blockAndItem(pit, ModTags.Blocks.MINI_FLOWERS, ModTags.Items.MINI_FLOWERS,
				IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA_SMALL);
			
			blockAndItem(pit, ModTags.Blocks.SPECIAL_FLOATING_FLOWERS, ModTags.Items.SPECIAL_FLOATING_FLOWERS,
				IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_FUNNY_SMALL,
				IncBlocks.FLOATING_SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA_SMALL);
		});
	}
}
