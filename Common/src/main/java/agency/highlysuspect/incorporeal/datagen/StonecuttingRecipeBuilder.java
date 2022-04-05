package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StonecuttingRecipeBuilder {
	public StonecuttingRecipeBuilder(ItemStack output) {
		this.output = output;
	}
	
	public static StonecuttingRecipeBuilder create(ItemStack output) {
		return new StonecuttingRecipeBuilder(output);
	}
	
	public static StonecuttingRecipeBuilder create(ItemLike output) {
		return create(new ItemStack(output));
	}
	
	//TODO: lift ItemStack restriction
	private final List<ItemStack> ingredients = new ArrayList<>();
	private final ItemStack output;
	private @Nullable String group;
	
	public StonecuttingRecipeBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}
	
	public StonecuttingRecipeBuilder input(Collection<ItemStack> stacks) {
		ingredients.addAll(stacks);
		return this;
	}
	
	public StonecuttingRecipeBuilder input(ItemStack... stacks) {
		return input(List.of(stacks));
	}
	
	public StonecuttingRecipeBuilder input0(Collection<ItemLike> inputs) {
		inputs.stream().map(ItemStack::new).forEach(ingredients::add);
		return this;
	}
	
	public StonecuttingRecipeBuilder input(ItemLike... input) {
		return input0(List.of(input));
	}
	
	public JsonObject toJson() {
		Ingredient coalescedIngredient = Ingredient.of(ingredients.stream());
		
		//Use vanilla machinery
		return new SingleItemRecipeBuilder.Result(null, RecipeSerializer.STONECUTTER, group, coalescedIngredient, output.getItem(), output.getCount(), null, null)
			.serializeRecipe();
	}
	
	//Paste from RunicAltarRecipeBuilder
	public void save(Consumer<JsonFile> fileConsumer) {
		save(fileConsumer, RecipeBuilder.getDefaultRecipeId(output.getItem()));
	}
	
	public void save(Consumer<JsonFile> fileConsumer, ResourceLocation id) {
		DataDsl.notAir(id); //Explode now if you try to save a recipe for unregistered item
		
		fileConsumer.accept(JsonFile.create(toJson(), "data", id.getNamespace(), "recipes", "stonecutting", id.getPath()));
	}
	
	//a group of items where you can stonecut one into all the other ones
	public static class Group {
		public Group(List<ItemStack> inputs) {
			this.inputs = inputs;
		}
		
		public static Group create(List<ItemStack> inputs) {
			return new Group(inputs);
		}
		
		public static Group create(ItemStack... inputs) {
			return create(List.of(inputs));
		}
		
		public static Group create0(List<ItemLike> inputs) {
			return new Group(inputs.stream().map(ItemStack::new).collect(Collectors.toList()));
		}
		
		public static Group create0(ItemLike... inputs) {
			return create0(List.of(inputs));
		}
		
		private final List<ItemStack> inputs;
		private String group;
		
		public Group group(String group) {
			this.group = group;
			return this;
		}
		
		@SuppressWarnings("SuspiciousListRemoveInLoop") //Yes, it's correct
		public void save(Consumer<JsonFile> fileConsumer) {
			for(int i = 0; i < inputs.size(); i++) {
				ItemStack output = inputs.get(i);
				
				//hide them away in a subfolder
				ResourceLocation id = DataDsl.prefixPath(RecipeBuilder.getDefaultRecipeId(output.getItem()), group);
				
				List<ItemStack> inputs = new ArrayList<>(this.inputs);
				inputs.remove(i);
				
				StonecuttingRecipeBuilder.create(output)
					.group(group)
					.input(inputs)
					.save(fileConsumer, id);
			}
		}
	}
}
