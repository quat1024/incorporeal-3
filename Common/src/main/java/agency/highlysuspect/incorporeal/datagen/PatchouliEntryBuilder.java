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
import org.apache.commons.lang3.mutable.MutableInt;
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

/**
 * Builder for Patchouli BookEntry jsons. A little bit sketchy, a little bit weird.
 * Doesn't include everything relating to bookentry json, just the things I need at the moment.
 */
public class PatchouliEntryBuilder {
	public PatchouliEntryBuilder(ResourceLocation bookId, String shortPath) {
		this.bookId = bookId;
		//pedantically, not sure if bookId.namespace is the right choice for these strings, but w/e
		this.path = new ResourceLocation(bookId.getNamespace(), shortPath);
		this.langBase = Stream.of(bookId.getNamespace(), bookId.getPath(), shortPath.replace('/', '.')).collect(Collectors.joining("."));
	}
	
	//Book's id.
	public final ResourceLocation bookId;
	
	//How you might refer to this entry from another Patchouli entry.
	//e.g. "incorporeal:ender/corporea_solidifier"
	public final ResourceLocation path;
	
	//Prefix for any auto-generated language keys.
	//e.g. "incorporeal.lexicon.ender.corporea_solidifier"
	public final String langBase;
	
	//Patchy will will crash with a null `name`: https://github.com/VazkiiMods/Patchouli/issues/509
	//For prototyping, allow a missing name
	public String name = "Unnamed Incorporeal Entry";
	
	//If "true", `name` is a literal string, and a translation key should be created for it.
	//If "false", `name` is already a translation key.
	public boolean addLangEntryForName = true;
	
	//Assorted patchy BookEntry properties.
	//Not all of them are mirrored across into this builder.
	public String category = "missing:category";
	public Item icon = Items.STONE;
	public boolean readByDefault = false;
	public @Nullable String advancement;
	public int color = -1;
	public int sortnum = -1;
	public Map<String, Integer> extraRecipeMappings = new HashMap<>();
	
	//Pages
	public List<Page> pages = new ArrayList<>();
	
	public PatchouliEntryBuilder save(DataGenerator datagen, Consumer<JsonFile> files) {
		JsonObject json = new JsonObject();
		EnUsRewriter rewriter = ((DatagenDuck) datagen).inc$getEnUsRewriter();
		
		//Name
		if(addLangEntryForName) {
			//The "name" string is a literal text. Instead of including it in the book directly,
			//add a lang key with the value of "name", and include that instead
			String langKey = langKey("name");
			json.addProperty("name", langKey);
			rewriter.associate(langKey, name);
		} else {
			//The name string is already a lang key. Include it directly in the book.
			json.addProperty("name", name);
		}
		
		//Misc properties
		json.addProperty("category", category);
		json.addProperty("icon", DataDsl.notAir(Registry.ITEM.getKey(icon)).toString());
		if(readByDefault) json.addProperty("read_by_default", true); //no need to include the property if it's false
		if(advancement != null) json.addProperty("advancement", advancement);
		if(color != -1) json.addProperty("color", Integer.toHexString(color));
		if(sortnum != -1) json.addProperty("sortnum", sortnum);
		
		//Pages
		JsonArray pagesArray = new JsonArray();
		MutableInt currentPage = new MutableInt(0); //(lambda capture)
		for(Page page : pages) {
			//Control flow's tied in a knot, watch the interlock:
			pagesArray.add(page.toJson((keySuffix, value) -> {
				//You're now in a helper function for toJson, that creates lang keys on demand
				//Prefix keySuffix with this entry's name and page number
				String key = langKey(currentPage.intValue(), keySuffix);
				//Add an entry in en_us.json
				rewriter.associate(key, value);
				//return the prefixed key
				return key;
			}));
			currentPage.increment();
		}
		json.add("pages", pagesArray);
		
		//Extra recipe mappings
		if(!extraRecipeMappings.isEmpty()) {
			JsonObject mappings = new JsonObject();
			extraRecipeMappings.forEach((k, v) -> mappings.addProperty(k, v));
			json.add("extra_recipe_mappings", mappings);
		}
		
		files.accept(JsonFile.create(json, "data", bookId.getNamespace(), "patchouli_books", bookId.getPath(), "en_us", "entries", path.getPath()));
		return this;
	}
	
	/// general property setters ///
	
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
	
	/// shortcuts ///
	
	public PatchouliEntryBuilder challenge(String name, String text) {
		return this.category("botania:challenges")
			.name(name)
			.readByDefault()
			.advancement("botania:main/mana_pool_pickup_lexicon")
			.sortnum(69)
			.checkboxQuest(name, text);
	}
	
	public PatchouliEntryBuilder devicesCategory() {
		return this.category("botania:devices");
	}
	
	public PatchouliEntryBuilder functionalFlowersCategory() {
		return this.category("botania:functional_flowers");
	}
	
	public PatchouliEntryBuilder enderCategory() {
		return this.category("botania:ender");
	}
	
	public PatchouliEntryBuilder miscCategory() {
		return this.category("botania:misc");
	}
	
	public PatchouliEntryBuilder toolsCategory() {
		return this.category("botania:tools");
	}
	
	public PatchouliEntryBuilder elven() {
		return this.advancement("botania:main/elf_lexicon_pickup");
	}
	
	/// pages ///
	
	//text pages
	public PatchouliEntryBuilder text(String text) {
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", "patchouli:text");
			json.addProperty("text", langKeyMaker.associate("text", text));
		});
		
		return this;
	}
	
	//skeleton for various types of simple recipe pages
	public PatchouliEntryBuilder recipe(String type, String recipeId, String text) {
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", type);
			json.addProperty("recipe", recipeId);
			if(text != null) json.addProperty("text", langKeyMaker.associate("text", text));
		});
		
		return this;
	}
	
	//said simple recipe pages
	public PatchouliEntryBuilder crafting(String recipeId, String text) {
		return recipe("patchouli:crafting", recipeId, text);
	}
	
	public PatchouliEntryBuilder petalApothecary(String recipeId, String text) {
		return recipe("botania:petal_apothecary", recipeId, text);
	}
	
	public PatchouliEntryBuilder runicAltar(String recipeId, String text) {
		return recipe("botania:runic_altar", recipeId, text);
	}
	
	//and some helpers that assume the recipe ID is the same as the item ID
	//(maybe plugging into the recipe datagen to get a better heuristic for that would be nice :eyes:)
	public PatchouliEntryBuilder crafting(ItemLike thing, String text) {
		return crafting(Registry.ITEM.getKey(thing.asItem()).toString(), text);
	}
	
	public PatchouliEntryBuilder petalApothecary(ItemLike thing, String text) {
		return petalApothecary(Registry.ITEM.getKey(thing.asItem()).toString(), text);
	}
	
	public PatchouliEntryBuilder runicAltar(ItemLike thing, String text) {
		return runicAltar(Registry.ITEM.getKey(thing.asItem()).toString(), text);
	}
	
	//the special botania:crafting_multi page
	public PatchouliEntryBuilder craftingMulti(Iterable<String> multi, String text) {
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", "botania:crafting_multi");
			json.add("recipes", jsonArrayStrings(multi));
			if(text != null) json.addProperty("text", langKeyMaker.associate("text", text));
		});
		
		return this;
	}
	
	//another helper that assumes recipe ID equals item ID
	public PatchouliEntryBuilder craftingMulti(Collection<? extends ItemLike> yes, String text) {
		return craftingMulti(yes.stream().map(ItemLike::asItem).map(Registry.ITEM::getKey).map(ResourceLocation::toString).collect(Collectors.toList()), text);
	}
	
	//quest page
	public PatchouliEntryBuilder checkboxQuest(String title, String text) {
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", "patchouli:quest");
			json.addProperty("title", langKeyMaker.associate("title", title));
			json.addProperty("text", langKeyMaker.associate("text", text));
		});
		
		return this;
	}
	
	//relations page
	public PatchouliEntryBuilder relations(@Nullable String title, @Nullable String text, Object... others) {
		//Strings and resourcelocations pass through, other PatchouliEntryBuilders turn into their entry path (so you can use them instead of strings)
		List<String> relations = Stream.of(others).map(other -> {
			if(other instanceof PatchouliEntryBuilder entry) return entry.path.toString();
			else return other.toString();
		}).collect(Collectors.toList());
		
		pages.add((json, langKeyMaker) -> {
			json.addProperty("type", "patchouli:relations");
			json.add("entries", jsonArrayStrings(relations));
			if(title != null) json.addProperty("title", langKeyMaker.associate("title", title));
			if(text != null) json.addProperty("text", langKeyMaker.associate("text", text));
		});
		
		return this;
	}
	
	public PatchouliEntryBuilder relations0(Object... others) {
		return relations(null, null, others);
	}
	
	/// pages internal ///
	
	public interface Page {
		void toJson(JsonObject json, LangKeyMaker langKeyMaker);
		
		default JsonObject toJson(LangKeyMaker langKeyMaker) {
			JsonObject yes = new JsonObject();
			toJson(yes, langKeyMaker);
			return yes;
		}
	}
	
	public interface LangKeyMaker {
		String associate(String keyPrefix, String value);
	}
	
	/// misc helpers ///
	
	//Stringify all the arguments, join them with periods, and prepend this entry's lang key prefix.
	//So, [0, "text"] -> "incorporeal.lexicon.category.entry.0.text"
	private String langKey(Object... ext) {
		return Stream.concat(Stream.of(langBase), Stream.of(ext).map(Object::toString)).collect(Collectors.joining("."));
	}
	
	//Converts an Iterable<String> into a json array of those strings. Boilerplatey.
	private static JsonArray jsonArrayStrings(Iterable<String> blah) {
		JsonArray yes = new JsonArray();
		blah.forEach(yes::add);
		return yes;
	}
}
