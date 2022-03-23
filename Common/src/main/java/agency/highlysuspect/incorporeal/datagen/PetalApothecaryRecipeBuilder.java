package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.lib.ModTags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record PetalApothecaryRecipeBuilder(ItemStack output, List<Ingredient> ingredients) {
	public static PetalApothecaryRecipeBuilder create(ItemStack output) {
		return new PetalApothecaryRecipeBuilder(output, new ArrayList<>());
	}
	
	public static PetalApothecaryRecipeBuilder create(ItemLike output) {
		return new PetalApothecaryRecipeBuilder(new ItemStack(output), new ArrayList<>());
	}
	
	public PetalApothecaryRecipeBuilder add(ItemLike item) {
		ingredients.add(Ingredient.of(item));
		return this;
	}
	
	public PetalApothecaryRecipeBuilder add(ItemLike item, int count) {
		for(int i = 0; i < count; i++) add(item);
		return this;
	}
	
	public PetalApothecaryRecipeBuilder add(TagKey<Item> item) {
		ingredients.add(Ingredient.of(item));
		return this;
	}
	
	public PetalApothecaryRecipeBuilder add(TagKey<Item> item, int count) {
		for(int i = 0; i < count; i++) add(item);
		return this;
	}
	
	public PetalApothecaryRecipeBuilder addPetals(DyeColor... petalColors) {
		for(DyeColor petalColor : petalColors) {
			add(ModTags.Items.getPetalTag(petalColor));
		}
		return this;
	}
	
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "botania:petal_apothecary");
		
		//Paste from PetalProvider.FinishedRecipe
		json.add("output", ItemNBTHelper.serializeStack(output));
		JsonArray ingredients = new JsonArray();
		for (Ingredient ingr : this.ingredients) {
			ingredients.add(ingr.toJson());
		}
		json.add("ingredients", ingredients);
		
		return json;
	}
	
	public void save(Consumer<JsonFile> fileConsumer) {
		save(fileConsumer, RecipeBuilder.getDefaultRecipeId(output.getItem()));
	}
	
	public void save(Consumer<JsonFile> fileConsumer, ResourceLocation id) {
		DataDsl.notAir(id); //Explode now if you try to save a recipe for unregistered item
		
		fileConsumer.accept(JsonFile.create(toJson(), "data", id.getNamespace(), "recipes", "petal_apothecary", id.getPath()));
	}
}
