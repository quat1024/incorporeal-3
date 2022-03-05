package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IncCommonBlockLootGen {
	public static void doIt(DataGenerator datagen, Consumer<JsonFile> files) {
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
	}
}
