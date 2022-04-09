package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.HashSet;
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
		Set<Block> selfDropExceptions = new HashSet<>();
		selfDropExceptions.add(IncBlocks.REDSTONE_ROOT_CROP); //Done manually
		selfDropExceptions.add(IncBlocks.CORPOREA_PYLON); //Done manually
		selfDropExceptions.addAll(IncBlocks.COMPRESSED_TATERS.values());
		
		for(Block b : INC_BLOCKS) {
			if(selfDropExceptions.contains(b)) continue;
			DataDsl.saveBlockLootTable(files, b, DataDsl.selfDrop(b));
		}
		
		for(Block tater : IncBlocks.COMPRESSED_TATERS.values()) {
			var entry = LootItem.lootTableItem(tater)
				.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY));
			LootPool.Builder pool = LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(entry)
				.when(ExplosionCondition.survivesExplosion());
			DataDsl.saveBlockLootTable(files, tater, LootTable.lootTable().withPool(pool));
		}
	}
}
