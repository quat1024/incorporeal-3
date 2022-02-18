package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * btw, this has nothing to do with vanilla's recipe builder system. I don't like it too much.
 */
public class RunicAltarRecipeBuilder {
	public RunicAltarRecipeBuilder(ItemStack output) {
		this.output = output;
	}
	
	public RunicAltarRecipeBuilder(ItemLike output) {
		this.output = new ItemStack(output);
	}
	
	public RunicAltarRecipeBuilder(ItemLike output, int count) {
		this.output = new ItemStack(output, count);
	}
	
	private final ItemStack output;
	private int mana = 5200;
	private List<Ingredient> ingredients = new ArrayList<>();
	
	public static final int TIER_1 = 5200; //water, fire, etc
	public static final int TIER_2 = 8000; //spring, autumn, etc
	public static final int TIER_3 = 12000; //lust, wrath, etc
	
	public RunicAltarRecipeBuilder mana(int mana) {
		this.mana = mana;
		return this;
	}
	
	public RunicAltarRecipeBuilder add(ItemLike item) {
		ingredients.add(Ingredient.of(item));
		return this;
	}
	
	public RunicAltarRecipeBuilder add(ItemLike item, int count) {
		for(int i = 0; i < count; i++) add(item);
		return this;
	}
	
	public RunicAltarRecipeBuilder add(Tag.Named<Item> item) {
		ingredients.add(Ingredient.of(item));
		return this;
	}
	
	public RunicAltarRecipeBuilder add(Tag.Named<Item> item, int count) {
		for(int i = 0; i < count; i++) add(item);
		return this;
	}
	
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "botania:runic_altar");
		
		//Paste from botnio RuneProvider#FinishedRecipe
		json.add("output", ItemNBTHelper.serializeStack(output));
		JsonArray ingredients = new JsonArray();
		for (Ingredient ingr : this.ingredients) {
			ingredients.add(ingr.toJson());
		}
		json.addProperty("mana", mana);
		json.add("ingredients", ingredients);
		
		return json;
	}
	
	public void save(Consumer<JsonDsl.JsonFile> fileConsumer) {
		save(fileConsumer, RecipeBuilder.getDefaultRecipeId(output.getItem()));
	}
	
	public void save(Consumer<JsonDsl.JsonFile> fileConsumer, ResourceLocation id) {
		fileConsumer.accept(JsonDsl.JsonFile.create("data", id.getNamespace(), "recipes", id.getPath(), toJson()));
	}
}
