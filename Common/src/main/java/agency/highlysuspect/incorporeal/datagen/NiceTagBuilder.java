package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Really simple builder, mainly intended for block/item tags.
 * No typechecking here, just pass it resourcelocations.
 */
public class NiceTagBuilder {
	public NiceTagBuilder(String classifier, ResourceLocation id) {
		this.classifier = classifier;
		this.id = id;
	}
	
	public final String classifier; // "blocks", "items" etc - the thing after "tags" in the file path
	public final ResourceLocation id;
	public boolean replace = false;
	public final List<String> entries = new ArrayList<>();
	
	public NiceTagBuilder replace() {
		this.replace = true;
		return this;
	}
	
	public NiceTagBuilder addBlocks(Collection<? extends Block> blocks) {
		blocks.stream().map(Registry.BLOCK::getKey).map(ResourceLocation::toString).forEach(entries::add);
		return this;
	}
	
	public NiceTagBuilder addBlocks(Block... blocks) {
		return addBlocks(List.of(blocks));
	}
	
	public NiceTagBuilder addItems(Collection<? extends ItemLike> itemLikes) {
		itemLikes.stream().map(ItemLike::asItem).map(Registry.ITEM::getKey).map(ResourceLocation::toString).forEach(entries::add);
		return this;
	}
	
	public NiceTagBuilder addItems(ItemLike... itemLikes) {
		return addItems(List.of(itemLikes));
	}
	
	public NiceTagBuilder addTags(TagKey<?>... tags) {
		Stream.of(tags).map(TagKey::location).map(ResourceLocation::toString).forEach(str -> entries.add("#" + str));
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
		
		public Duplex add(Collection<? extends Block> wah) {
			blocks.addBlocks(wah);
			items.addItems(wah);
			return this;
		}
		
		public void save(Consumer<JsonFile> fileConsumer) {
			blocks.save(fileConsumer);
			items.save(fileConsumer);
		}
	}
}
