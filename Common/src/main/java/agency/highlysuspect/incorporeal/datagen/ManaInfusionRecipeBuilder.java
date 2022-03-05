package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.crafting.StateIngredientHelper;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.function.Consumer;

public class ManaInfusionRecipeBuilder {
	public ManaInfusionRecipeBuilder(ItemStack output, int mana) {
		this.output = output;
		this.mana = mana;
	}
	
	public static ManaInfusionRecipeBuilder create(ItemStack output, int mana) {
		return new ManaInfusionRecipeBuilder(output, mana);
	}
	
	public static ManaInfusionRecipeBuilder create(ItemLike output, int mana) {
		return create(new ItemStack(output), mana);
	}
	
	private final ItemStack output;
	private final int mana;
	
	private Ingredient input;
	private String group = "";
	private @Nullable StateIngredient catalyst;
	
	public ManaInfusionRecipeBuilder input(Ingredient input) {
		this.input = input;
		return this;
	}
	
	public ManaInfusionRecipeBuilder input(ItemLike input) {
		return input(Ingredient.of(input));
	}
	
	public ManaInfusionRecipeBuilder input(Tag.Named<Item> input) {
		return input(Ingredient.of(input));
	}
	
	public ManaInfusionRecipeBuilder group(String group) {
		this.group = group;
		return this;
	}
	
	public ManaInfusionRecipeBuilder catalyst(StateIngredient catalyst) {
		this.catalyst = catalyst;
		return this;
	}
	
	public ManaInfusionRecipeBuilder alchemyCatalyst() {
		return catalyst(StateIngredientHelper.of(ModBlocks.alchemyCatalyst));
	}
	
	public ManaInfusionRecipeBuilder conjurationCatalyst() {
		return catalyst(StateIngredientHelper.of(ModBlocks.conjurationCatalyst));
	}
	
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "botania:mana_infusion");
		
		//Paste from ManaInfusionProvider.FinishedRecipe
		json.add("input", input.toJson());
		json.add("output", ItemNBTHelper.serializeStack(output));
		json.addProperty("mana", mana);
		if (!group.isEmpty()) {
			json.addProperty("group", group);
		}
		if (catalyst != null) {
			json.add("catalyst", catalyst.serialize());
		}
		
		return json;
	}
	
	//Paste from RunicAltarRecipeBuilder
	public void save(Consumer<JsonFile> fileConsumer) {
		save(fileConsumer, RecipeBuilder.getDefaultRecipeId(output.getItem()));
	}
	
	public void save(Consumer<JsonFile> fileConsumer, ResourceLocation id) {
		DataDsl.notAir(id); //Explode now if you try to save a recipe for unregistered item
		
		fileConsumer.accept(JsonFile.create(toJson(), "data", id.getNamespace(), "recipes", "mana_infusion", id.getPath()));
	}
}
