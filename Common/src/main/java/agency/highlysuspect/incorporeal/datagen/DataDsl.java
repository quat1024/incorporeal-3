package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Pile of utilities for writing datagens that create serverside data.
 */
public class DataDsl {
	/// Common helpers ///
	
	//Throws an exception when you pass minecraft:air
	private static final ResourceLocation AIR = new ResourceLocation("minecraft", "air");
	public static ResourceLocation notAir(ResourceLocation in) {
		if(in.equals(AIR)) throw new IllegalArgumentException("minecraft:air");
		else return in;
	}
	
	public static ResourceLocation mapPath(ResourceLocation in, UnaryOperator<String> mapper) {
		return new ResourceLocation(in.getNamespace(), mapper.apply(in.getPath()));
	}
	
	public static ResourceLocation prefixPath(ResourceLocation in, String pathPrefix) {
		return new ResourceLocation(in.getNamespace(), pathPrefix + '/' + in.getPath());
	}
	
	/// Simple data provider interface ///
	
	//Don't be afraid of the double nested Consumer<Consumer<T>>.
	//It gives you a bucket to throw JsonFiles into, you fill the bucket, and they get written to disk.
	//Simple as that.
	public static void addProvider(DataGenerator datagen, String name, BiConsumer<DataGenerator, Consumer<JsonFile>> generator) {
		datagen.addProvider(new DataProvider() {
			@Override
			public void run(HashCache hashCache) throws IOException {
				//create a bucket
				List<JsonFile> bucket = new ArrayList<>();
				
				//allow the caller to fill it with json files
				generator.accept(datagen, bucket::add);
				
				//write them all out
				bucket.forEach(f -> {
					Inc.LOGGER.info("Saving " + String.join("/", f.pathSegments()));
					f.save(datagen, hashCache);
				});
			}
			
			@Override
			public String getName() {
				return name;
			}
		});
	}
	
	/// General data builders ///
	
	//tags
	public static NiceTagBuilder blockTag(ResourceLocation tagRl) {
		return new NiceTagBuilder("blocks", tagRl);
	}
	
	public static NiceTagBuilder blockTag(TagKey<Block> tag) {
		return blockTag(tag.location());
	}
	
	public static NiceTagBuilder itemTag(ResourceLocation tagRl) {
		return new NiceTagBuilder("items", tagRl);
	}
	
	public static NiceTagBuilder itemTag(TagKey<Item> tag) {
		return itemTag(tag.location());
	}
	
	public static NiceTagBuilder.Duplex blockAndItemTag(ResourceLocation tagRl) {
		return new NiceTagBuilder.Duplex(tagRl);
	}
	
	public static NiceTagBuilder.Duplex blockAndItemTag(TagKey<Block> tag) {
		return blockAndItemTag(tag.location());
	}
	
	/// Block loot tables ///
	
	//Instead of wrapping LootTable.Builder with a nice save(Consumer<JsonFile>) method, i just provide this, out of laziness.
	//Also takes the Block you're making the loot table for, because yeah, the loot table builder doesn't know that.
	//Just imagine this is a cute kotlin extension method on LootTable.Builder i guess.
	public static void saveBlockLootTable(Consumer<JsonFile> fileConsumer, Block block, LootTable.Builder table) {
		ResourceLocation rl = notAir(Registry.BLOCK.getKey(block));
		fileConsumer.accept(JsonFile.create(serializeBlockLootTable(table), "data", rl.getNamespace(), "loot_tables/blocks", rl.getPath()));
	}
	
	public static JsonElement serializeBlockLootTable(LootTable.Builder builder) {
		return LootTables.serialize(builder.setParamSet(LootContextParamSets.BLOCK).build());
	}
	
	//copy from Botania ("genRegular")
	public static LootTable.Builder selfDrop(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
			.when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}
}
