package agency.highlysuspect.incorporeal.datagen;

import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static agency.highlysuspect.incorporeal.datagen.JsonDsl.*;

public class DataDsl {
	private static final ResourceLocation AIR = new ResourceLocation("minecraft", "air");
	public static ResourceLocation notAir(ResourceLocation in) {
		if(in.equals(AIR)) throw new IllegalArgumentException("minecraft:air");
		else return in;
	}
	
	public static void addProvider(DataGenerator datagen, String name, Consumer<Consumer<JsonFile>> generator) {
		datagen.addProvider(new DataProvider() {
			@Override
			public void run(HashCache hashCache) throws IOException {
				List<JsonFile> files = new ArrayList<>();
				generator.accept(files::add);
				
				files.forEach(f -> f.save(datagen, hashCache));
			}
			
			@Override
			public String getName() {
				return name;
			}
		});
	}
	
	public static JsonFile blockTag(ResourceLocation tagRl, Block... entries) {
		return obj(
			"replace", false,
			"values", array0(entries)
		).fileOf("data", tagRl.getNamespace(), "tags/blocks", tagRl.getPath());
	}
	
	public static JsonFile blockTag(String tag, Block... entries) {
		return blockTag(new ResourceLocation(tag), entries);
	}
	
	public static JsonFile blockTag(Tag.Named<Block> tag, Block... entries) {
		return blockTag(tag.getName().toString(), entries);
	}
	
	public static JsonFile itemTag(ResourceLocation tagRl, ItemLike... entries) {
		return obj(
			"replace", false,
			"values", array0(entries)
		).fileOf("data", tagRl.getNamespace(), "tags/items", tagRl.getPath());
	}
	
	public static JsonFile itemTag(String tag, ItemLike... entries) {
		return itemTag(new ResourceLocation(tag), entries);
	}
	
	public static JsonFile itemTag(Tag.Named<Item> tag, ItemLike... entries) {
		return itemTag(tag.getName().toString(), entries);
	}
	
	public static void blockAndItem(Consumer<JsonFile> out, Tag.Named<Block> blockTag, Tag.Named<Item> itemTag, Block... entries) {
		out.accept(blockTag(blockTag, entries));
		out.accept(itemTag(itemTag, entries));
	}
	
	public static JsonFile selfDrop(Block b) {
		ResourceLocation rl = notAir(Registry.BLOCK.getKey(b));
		
		return obj(
			"type", "minecraft:block",
			"pools", array(
				obj(
					"name", "self",
					"rolls", 1,
					"entries", array(
						obj(
							"type", "minecraft:item",
							"name", b.asItem()
						)
					),
					"conditions", array(
						obj(
							"condition", "minecraft:survives_explosion"
						)
					)
				)
			)
		).fileOf("data", rl.getNamespace(), "loot_tables/blocks", rl.getPath());
	}
	
	public static void main(String[] args) {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		System.out.println(selfDrop(Blocks.STONE_BUTTON));
		System.out.println(selfDrop(Blocks.STONE_BUTTON).value().prettyPrint());
	}
}
