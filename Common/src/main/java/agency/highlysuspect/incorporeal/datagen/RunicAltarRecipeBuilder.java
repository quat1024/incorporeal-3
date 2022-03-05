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
 * Quick builder for Runic Altar recipes.
 * Has nothing to do with the vanilla ShapedRecipeBuilder/FinishedRecipe/etc systems, they're a bit too heavy, I think.
 */
public record RunicAltarRecipeBuilder(ItemStack output, int mana, List<Ingredient> ingredients) {
	public static RunicAltarRecipeBuilder create(ItemStack output, int mana) {
		return new RunicAltarRecipeBuilder(output, mana, new ArrayList<>());
	}
	
	public static RunicAltarRecipeBuilder create(ItemLike output, int mana) {
		return new RunicAltarRecipeBuilder(new ItemStack(output), mana, new ArrayList<>());
	}
	
	public static RunicAltarRecipeBuilder create(ItemLike output, int count, int mana) {
		return new RunicAltarRecipeBuilder(new ItemStack(output, count), mana, new ArrayList<>());
	}
	
	public static final int TIER_1 = 5200; //water, fire, etc
	public static final int TIER_2 = 8000; //spring, autumn, etc
	public static final int TIER_3 = 12000; //lust, wrath, etc
	
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
	
	public void save(Consumer<JsonFile> fileConsumer) {
		save(fileConsumer, RecipeBuilder.getDefaultRecipeId(output.getItem()));
	}
	
	public void save(Consumer<JsonFile> fileConsumer, ResourceLocation id) {
		DataDsl.notAir(id); //Explode now if you try to save a recipe for unregistered item
		
		fileConsumer.accept(JsonFile.create(toJson(), "data", id.getNamespace(), "recipes", id.getPath()));
	}
}
