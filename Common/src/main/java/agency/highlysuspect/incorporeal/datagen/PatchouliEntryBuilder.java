package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This is not a builder for everything relating to BookEntry.
 * I will add more things as I need them.
 */
public class PatchouliEntryBuilder {
	public PatchouliEntryBuilder(ResourceLocation bookId, String shortPath) {
		this.bookId = bookId;
		this.filePath = shortPath;
		this.patchyPath = Inc.MODID + ":" + shortPath;
		this.langBase = Inc.MODID + ".lexica." + shortPath.replace('/', '.');
	}
	
	//Book's id.
	public final ResourceLocation bookId;
	
	//Where the entry .json file will end up (not namespaced - book id already takes care of the namespace)
	//e.g. "ender/corporea_solidifier"
	public final String filePath;
	
	//How to refer to this entry from other Patchouli pages.
	//e.g. "incorporeal:ender/corporea_solidifier"
	public final String patchyPath;
	
	//Prefix for any auto-generated language keys.
	//e.g. "incorporeal.lexica.ender.corporea_solidifier"
	public final String langBase;
	
	//Patchy will will crash with a null `name`: https://github.com/VazkiiMods/Patchouli/issues/509
	//For prototyping, allow a missing name
	public String name = "Unnamed Incorporeal Entry";
	
	//If "true", `name` is a literal string, and a translation key should be created for it.
	//If "false", `name` is already a translation key.
	public boolean addLangEntryForName = true;
	
	//Assorted patchy BookEntry properties. Not all of them are mirrored across into this builder.
	public String category = "missing:category";
	public Item icon = Items.STONE;
	public boolean readByDefault = false;
	public @Nullable String advancement;
	public int color = -1;
	public int sortnum = -1;
	public Map<String, Integer> extraRecipeMappings = new HashMap<>();
	
	private int currentPage = 0;
	private EnUsRewriter rewriter = null; //set and used below
	public List<Page> pages = new ArrayList<>();
	
	public PatchouliEntryBuilder save(DataGenerator datagen, Consumer<JsonFile> files) {
		JsonObject json = new JsonObject();
		rewriter = ((DatagenDuck) datagen).inc$getEnUsRewriter();
		
		if(addLangEntryForName) {
			String langKey = langKey("name");
			json.addProperty("name", langKey);
			rewriter.associate(langKey, name);
		} else {
			json.addProperty("name", name);
		}
		
		json.addProperty("category", category);
		json.addProperty("icon", DataDsl.notAir(Registry.ITEM.getKey(icon)).toString());
		if(readByDefault) json.addProperty("read_by_default", true);
		if(advancement != null) json.addProperty("advancement", advancement);
		if(color != -1) json.addProperty("color", Integer.toHexString(color));
		if(sortnum != -1) json.addProperty("sortnum", sortnum);
		
		JsonArray pagesArray = new JsonArray();
		currentPage = 0;
		for(Page page : pages) {
			pagesArray.add(page.toJson(this::generatePageLangKey));
			currentPage++;
		}
		json.add("pages", pagesArray);
		
		if(!extraRecipeMappings.isEmpty()) {
			JsonObject yes = new JsonObject();
			extraRecipeMappings.forEach((k, v) -> yes.addProperty(k, v));
			json.add("extra_recipe_mappings", yes);
		}
		
		files.accept(JsonFile.create(json, "data", bookId.getNamespace(), "patchouli_books", bookId.getPath(), "en_us", "entries", filePath));
		
		return this;
	}
	
	public PatchouliEntryBuilder name(String name) {
		this.name = name;
		this.addLangEntryForName = true;
		return this;
	}
	
	public PatchouliEntryBuilder name(ItemLike item) {
		this.name = item.asItem().getDescriptionId();
		this.addLangEntryForName = false;
		return this;
	}
	
	public PatchouliEntryBuilder category(String category) {
		this.category = category;
		return this;
	}
	
	public PatchouliEntryBuilder icon(ItemLike icon) {
		this.icon = icon.asItem();
		return this;
	}
	
	public PatchouliEntryBuilder nameAndIcon(ItemLike icon) {
		return name(icon).icon(icon);
	}
	
	public PatchouliEntryBuilder readByDefault() {
		this.readByDefault = true;
		return this;
	}
	
	public PatchouliEntryBuilder advancement(String advancement) {
		this.advancement = advancement;
		return this;
	}
	
	public PatchouliEntryBuilder color(int color) {
		this.color = color;
		return this;
	}
	
	public PatchouliEntryBuilder sortnum(int sortnum) {
		this.sortnum = sortnum;
		return this;
	}
	
	public PatchouliEntryBuilder extraRecipeMapping(ItemLike xd, int page) {
		this.extraRecipeMappings.put(Registry.ITEM.getKey(xd.asItem()).toString(), page);
		return this;
	}
	
	public PatchouliEntryBuilder extraRecipeMapping(ItemLike xd) {
		return extraRecipeMapping(xd, 0);
	}
	
	/// utils ///
	
	public PatchouliEntryBuilder challenge(String name, String text) {
		return this.category("botania:challenges")
			.name(name)
			.readByDefault()
			.advancement("botania:main/mana_pool_pickup_lexicon")
			.sortnum(69)
			.checkboxQuest(name, text);
	}
	
	public PatchouliEntryBuilder elven() {
		return this.advancement("botania:main/elf_lexicon_pickup");
	}
	
	/// pages ///
	
	public PatchouliEntryBuilder text(String text) {
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", patchouliId("text").toString());
			json.addProperty("text", langKeyMaker.apply("text", text));
		});
		
		return this;
	}
	
	public PatchouliEntryBuilder crafting(String recipeId, String text) {
		return recipe(patchouliId("crafting"), recipeId, text);
	}
	
	public PatchouliEntryBuilder crafting(ItemLike thing, String text) {
		//Assumes the item's crafting recipe is named after the item
		return crafting(Registry.ITEM.getKey(thing.asItem()).toString(), text);
	}
	
	public PatchouliEntryBuilder petalApothecary(String recipeId, String text) {
		return recipe(Inc.botaniaId("petal_apothecary"), recipeId, text);
	}
	
	public PatchouliEntryBuilder petalApothecary(ItemLike thing, String text) {
		//Assumes the item's crafting recipe is named after the item
		return petalApothecary(Registry.ITEM.getKey(thing.asItem()).toString(), text);
	}
	
	public PatchouliEntryBuilder runicAltar(String recipeId, String text) {
		return recipe(Inc.botaniaId("runic_altar"), recipeId, text);
	}
	
	public PatchouliEntryBuilder runicAltar(ItemLike thing, String text) {
		//Assumes the item's crafting recipe is named after the item
		return runicAltar(Registry.ITEM.getKey(thing.asItem()).toString(), text);
	}
	
	public PatchouliEntryBuilder recipe(ResourceLocation type, String recipeId, String text) {
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", type.toString());
			json.addProperty("recipe", recipeId);
			if(text != null) json.addProperty("text", langKeyMaker.apply("text", text));
		});
		
		return this;
	}
	
	public PatchouliEntryBuilder craftingMulti(Iterable<String> multi, String text) {
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", Inc.botaniaId("crafting_multi").toString());
			json.add("recipes", jsonArrayStrings(multi));
			if(text != null) json.addProperty("text", langKeyMaker.apply("text", text));
		});
		
		return this;
	}
	
	public PatchouliEntryBuilder craftingMulti(Collection<? extends ItemLike> yes, String text) {
		return craftingMulti(yes.stream().map(ItemLike::asItem).map(Registry.ITEM::getKey).map(ResourceLocation::toString).collect(Collectors.toList()), text);
	}
	
	public PatchouliEntryBuilder checkboxQuest(String title, String text) {
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", patchouliId("quest").toString());
			json.addProperty("title", langKeyMaker.apply("title", title));
			json.addProperty("text", langKeyMaker.apply("text", text));
		});
		
		return this;
	}
	
	public PatchouliEntryBuilder relations(@Nullable String title, @Nullable String text, Object... others) {
		List<String> relations = Stream.of(others).map(other -> {
			if(other instanceof PatchouliEntryBuilder entry) return entry.patchyPath;
			else return other.toString();
		}).collect(Collectors.toList());
		
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", patchouliId("relations").toString());
			json.add("entries", jsonArrayStrings(relations));
			if(title != null) json.addProperty("title", langKeyMaker.apply("title", title));
			if(text != null) json.addProperty("text", langKeyMaker.apply("text", text));
		});
		
		return this;
	}
	
	public PatchouliEntryBuilder relations0(Object... others) {
		return relations(null, null, others);
	}
	
	/// pages internal ///
	
	@SuppressWarnings("InnerClassMayBeStatic")
	public interface Page {
		void toJson(JsonObject json, BiFunction<String, String, String> langKeyMaker);
		
		default JsonObject toJson(BiFunction<String, String, String> langKeyMaker) {
			JsonObject yes = new JsonObject();
			toJson(yes, langKeyMaker);
			return yes;
		}
	}
	
	/// misc helpers ///
	
	private String langKey(Object... ext) {
		return Stream.concat(Stream.of(langBase), Stream.of(ext).map(Object::toString)).collect(Collectors.joining("."));
	}
	
	private String generatePageLangKey(String keySuffix, String value) {
		String key = langKey(currentPage, keySuffix); //compute the full lang key
		rewriter.associate(key, value); //associate the key with the value
		return key; //return completed key
	}
	
	private static ResourceLocation patchouliId(String path) {
		return new ResourceLocation("patchouli", path);
	}
	
	private static JsonArray jsonArrayStrings(Iterable<String> blah) {
		JsonArray yes = new JsonArray();
		blah.forEach(yes::add);
		return yes;
	}
}
