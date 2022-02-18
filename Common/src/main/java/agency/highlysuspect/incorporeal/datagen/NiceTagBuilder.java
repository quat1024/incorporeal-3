package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class NiceTagBuilder {
	public NiceTagBuilder(String classifier, ResourceLocation id) {
		this.classifier = classifier;
		this.id = id;
	}
	
	public final String classifier; // "blocks", "items" etc. thing after "tags" in the file path
	public final ResourceLocation id;
	public boolean replace = false;
	public final List<String> entries = new ArrayList<>();
	
	public NiceTagBuilder replace() {
		this.replace = true;
		return this;
	}
	
	public NiceTagBuilder addBlocks(Block... blocks) {
		Stream.of(blocks).map(Registry.BLOCK::getKey).map(ResourceLocation::toString).forEach(entries::add);
		return this;
	}
	
	public NiceTagBuilder addItems(ItemLike... itemLikes) {
		Stream.of(itemLikes).map(ItemLike::asItem).map(Registry.ITEM::getKey).map(ResourceLocation::toString).forEach(entries::add);
		return this;
	}
	
	public NiceTagBuilder addTags(Tag.Named<?>... tags) {
		Stream.of(tags).map(Tag.Named::getName).map(ResourceLocation::toString).forEach(str -> entries.add("#" + str));
		return this;
	}
	
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("replace", replace);
		
		JsonArray array = new JsonArray();
		entries.forEach(array::add);
		json.add("values", array);
		
		return json;
	}
	
	public void save(Consumer<JsonFile> fileConsumer) {
		fileConsumer.accept(JsonFile.create(toJson(), "data", id.getNamespace(), "tags", classifier, id.getPath()));
	}
	
	public static class Duplex {
		public Duplex(ResourceLocation id) {
			this.blocks = new NiceTagBuilder("blocks", id);
			this.items = new NiceTagBuilder("items", id);
		}
		
		public final NiceTagBuilder blocks;
		public final NiceTagBuilder items;
		
		public Duplex add(Block... blockItems) {
			blocks.addBlocks(blockItems);
			items.addItems(blockItems);
			return this;
		}
		
		public void save(Consumer<JsonFile> fileConsumer) {
			blocks.save(fileConsumer);
			items.save(fileConsumer);
		}
	}
}
