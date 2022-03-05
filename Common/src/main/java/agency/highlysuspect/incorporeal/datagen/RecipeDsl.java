package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import vazkii.botania.common.lib.ModTags;
import vazkii.botania.mixin.AccessorRecipeProvider;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Pile of utilities for writing various types of recipes.
 */
public class RecipeDsl {
	/// DSL ///
	
	public static NiceShapedRecipeBuilder shaped(ItemLike out, int count, String... lines) {
		ShapedRecipeBuilder inner = new ShapedRecipeBuilder(out, count);
		for(String line : lines) inner.pattern(line);
		return new NiceShapedRecipeBuilder(inner);
	}
	
	public static NiceShapedRecipeBuilder shaped(ItemLike out, String... lines) {
		return shaped(out, 1, lines);
	}
	
	public static NiceShapelessRecipeBuilder shapeless(ItemLike out, int count) {
		return new NiceShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(out, count));
	}
	
	public static NiceShapelessRecipeBuilder shapeless(ItemLike out) {
		return shapeless(out, 1);
	}
	
	public static NiceShapedRecipeBuilder compress9(ItemLike small, ItemLike big) {
		return shaped(big, "###", "###", "###").define("#", small);
	}
	
	public static NiceShapelessRecipeBuilder uncompressTo9(ItemLike big, ItemLike small) {
		return shapeless(small, 9).add(big);
	}
	
	public static NiceShapelessRecipeBuilder floatingFlower(ItemLike notFloating, ItemLike floating) {
		return shapeless(floating).add(notFloating).add(ModTags.Items.FLOATING_FLOWERS);
	}
	
	public static RunicAltarRecipeBuilder runic(ItemLike out, int mana) {
		return RunicAltarRecipeBuilder.create(out, mana);
	}
	
	public static PetalApothecaryRecipeBuilder apothecary(ItemLike out) {
		return PetalApothecaryRecipeBuilder.create(out);
	}
	
	public static ManaInfusionRecipeBuilder infusion(ItemLike out, int mana) {
		return ManaInfusionRecipeBuilder.create(out, mana);
	}
	
	public static ManaInfusionRecipeBuilder miniFlower(ItemLike big, ItemLike small) {
		return infusion(small, 2500).input(big).alchemyCatalyst().group("botania:flower_shrinking");
	}
	
	/// Some builders ///
	
	//wrapper of ShapedRecipeBuilder that automatically adds advancement criteria for each recipe ingredient, and plugs into the JsonFile ecosystem.
	//(doesn't wrap the whole thing, just the things i need)
	public static record NiceShapedRecipeBuilder(ShapedRecipeBuilder inner) {
		public NiceShapedRecipeBuilder define(Object key, ItemLike ingredient) {
			inner.define(toChar(key), ingredient);
			inner.unlockedBy(DataDsl.notAir(Registry.ITEM.getKey(ingredient.asItem())).getPath(), conditionsFromItem(ingredient));
			return this;
		}
		
		public NiceShapedRecipeBuilder define(Object key, Tag.Named<Item> ingredient) {
			inner.define(toChar(key), ingredient);
			inner.unlockedBy(ingredient.getName().getPath(), conditionsFromTag(ingredient));
			return this;
		}
		
		public NiceShapedRecipeBuilder group(String group) {
			inner.group(group);
			return this;
		}
		
		public void save(Consumer<JsonFile> files) {
			RecipeDsl.save(files, inner);
		}
		
		public void save(Consumer<JsonFile> files, ResourceLocation id) {
			RecipeDsl.save(files, inner, id);
		}
	}
	
	//ditto for shapeless recipes
	public static record NiceShapelessRecipeBuilder(ShapelessRecipeBuilder inner) {
		public NiceShapelessRecipeBuilder add(ItemLike ingredient) {
			return add(ingredient, 1);
		}
		
		public NiceShapelessRecipeBuilder add(Tag.Named<Item> tag) {
			return add(tag, 1);
		}
		
		public NiceShapelessRecipeBuilder add(ItemLike ingredient, int count) {
			inner.requires(ingredient, count);
			inner.unlockedBy(DataDsl.notAir(Registry.ITEM.getKey(ingredient.asItem())).getPath(), conditionsFromItem(ingredient));
			return this;
		}
		
		public NiceShapelessRecipeBuilder add(Tag.Named<Item> tag, int count) {
			inner.requires(Ingredient.of(tag), count); //isn't a helper method for ing+count for tags, just for Ingredients and ItemLikes, lol
			inner.unlockedBy(tag.getName().getPath(), conditionsFromTag(tag));
			return this;
		}
		
		public void save(Consumer<JsonFile> files) {
			RecipeDsl.save(files, inner);
		}
		
		public void save(Consumer<JsonFile> files, ResourceLocation id) {
			RecipeDsl.save(files, inner, id);
		}
	}
	
	/// File saving for the above recipe builders ///
	
	public static void save(Consumer<JsonFile> fileConsumer, RecipeBuilder builder) {
		save(fileConsumer, builder, RecipeBuilder.getDefaultRecipeId(builder.getResult()));
	}
	
	//shaped a little bit after the lambda in RecipeProvider#run
	public static void save(Consumer<JsonFile> fileConsumer, RecipeBuilder builder, ResourceLocation id) {
		DataDsl.notAir(id); //Explode now if you try to save a recipe for unregistered item
		
		builder.save(finishedRecipe -> {
			JsonObject recipeJson = finishedRecipe.serializeRecipe();
			fileConsumer.accept(JsonFile.create(recipeJson, "data", id.getNamespace(), "recipes", id.getPath()));
			
			JsonObject advancementJson = finishedRecipe.serializeAdvancement();
			if(advancementJson != null) {
				fileConsumer.accept(JsonFile.create(advancementJson, "data", id.getNamespace(), "advancements/recipes/" + id.getNamespace(), id.getPath()));
			}
		}, id);
	}
	
	/// Assorted utilities ///
	
	//char | string -> char, because i keep mistyping the argument to ShapedRecipeBuilder#define
	private static char toChar(Object key) {
		if(key instanceof Character c) return c;
		else if(key instanceof String s) return s.charAt(0);
		else throw new IllegalArgumentException();
	}
	
	//Uses botania's accessor for RecipeProvider#inventoryTrigger. also i copypasted this from botania.
	public static InventoryChangeTrigger.TriggerInstance conditionsFromTag(Tag<Item> tag) {
		return AccessorRecipeProvider.botania_condition(ItemPredicate.Builder.item().of(tag).build());
	}
	
	public static InventoryChangeTrigger.TriggerInstance conditionsFromItem(ItemLike item) {
		return AccessorRecipeProvider.botania_condition(ItemPredicate.Builder.item().of(item).build());
	}
}
