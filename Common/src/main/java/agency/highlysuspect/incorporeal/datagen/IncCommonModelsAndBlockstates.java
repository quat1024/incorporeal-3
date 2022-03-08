package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.CompressedTaterUtil;
import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.CompressedTinyPotatoBlock;
import agency.highlysuspect.incorporeal.block.CrappyComparatorBlock;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.RedstoneRootCropBlock;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.mixin.TextureSlotAccessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.mixin.AccessorBlockModelGenerators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IncCommonModelsAndBlockstates {
	//Like botania BlockstateProvider
	private static final List<BlockStateGenerator> stateGenerators = new ArrayList<>();
	private static final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();
	private static final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = models::put;
	
	public static void doIt(DataGenerator generator, Consumer<JsonFile> files) {
		/// Simple items ///
		itemGenerated(IncItems.FRACTURED_SPACE_ROD, Inc.id("item/fractured_space_rod/tex"));
		itemGenerated(IncItems.TICKET_CONJURER, Inc.id("item/ticket_conjurer/tex"));
		
		/// Corporetics ///
		singleVariantCubeColumn(IncBlocks.CORPOREA_SOLIDIFIER,
			Inc.id("block/corporea_solidifier/side"),
			Inc.id("block/corporea_solidifier/top_bottom")
		);
		itemBlockModelParent(IncBlocks.CORPOREA_SOLIDIFIER);
		
		singleVariantThreeHighBottomTop(IncBlocks.FRAME_TINKERER,
			Inc.id("block/frame_tinkerer/bottom"),
			Inc.id("block/frame_tinkerer/top"),
			Inc.id("block/frame_tinkerer/side")
		);
		itemBlockModelParent(IncBlocks.FRAME_TINKERER);
		
		redString(IncBlocks.RED_STRING_LIAR, Inc.id("block/red_string_liar/side"));
		itemBlockModelParent(IncBlocks.RED_STRING_LIAR);
		
		/// Soul cores ///
		
		ModelTemplate soulCoreTemplate = template(Inc.id("block/soul_core"), TextureSlot.TEXTURE);
		
		singleVariantBlockState(IncBlocks.ENDER_SOUL_CORE,
			soulCoreTemplate.create(IncBlocks.ENDER_SOUL_CORE, txmap(TextureSlot.TEXTURE, Inc.id("block/soul_cores/ender")), modelOutput));
		itemBlockModelParent(IncBlocks.ENDER_SOUL_CORE);
		
		singleVariantBlockState(IncBlocks.POTION_SOUL_CORE,
			soulCoreTemplate.create(IncBlocks.POTION_SOUL_CORE, txmap(TextureSlot.TEXTURE, Inc.id("block/soul_cores/potion")), modelOutput));
		itemBlockModelParent(IncBlocks.POTION_SOUL_CORE);
		
		//this one's not a blockitem
		soulCoreTemplate.create(ModelLocationUtils.getModelLocation(IncItems.SOUL_CORE_FRAME),
			txmap(TextureSlot.TEXTURE, Inc.id("block/soul_cores/frame")), modelOutput);
		
		/// Natural devices ///
		//Redstone root crop
		TextureSlot TX_HAT = txslot("hat");
		ModelTemplate shortCrossWithHat = template(Inc.id("block/natural_devices/short_cross_with_hat"), TextureSlot.CROSS, TX_HAT);
		stateGenerators.add(MultiVariantGenerator.multiVariant(IncBlocks.REDSTONE_ROOT_CROP).with(PropertyDispatch.property(RedstoneRootCropBlock.AGE).generate(age -> modelv(shortCrossWithHat.create(
			Inc.id("block/natural_devices/crop_" + age),
			txmap(
				TextureSlot.CROSS, Inc.id("block/natural_devices/cross/" + (age >= 4 ? "large" : "small")),
				TX_HAT, Inc.id("block/natural_devices/top/growing_" + age)
			), modelOutput)))));
		
		//Natural repeater
		stateGenerators.add(MultiVariantGenerator.multiVariant(IncBlocks.NATURAL_REPEATER)
			.with(AccessorBlockModelGenerators.horizontalDispatch())
			.with(PropertyDispatch.property(BlockStateProperties.POWERED)
				.generate(powered -> modelv(naturalRepeaterModel(powered)))));
		itemDelegatedTo(IncBlocks.NATURAL_REPEATER, naturalRepeaterModel(false));
		
		//Natural comparator
		stateGenerators.add(MultiVariantGenerator.multiVariant(IncBlocks.NATURAL_COMPARATOR)
			.with(AccessorBlockModelGenerators.horizontalDispatch())
			.with(PropertyDispatch.properties(BlockStateProperties.POWERED, CrappyComparatorBlock.SENSITIVE)
				.generate((powered, sensitive) -> modelv(naturalComparatorModel(powered, sensitive)))));
		itemDelegatedTo(IncBlocks.NATURAL_COMPARATOR, naturalComparatorModel(false, false));
		
		/// Flowers ///
		flowerWithItemModel(IncBlocks.SANVOCALIA, Inc.id("block/sanvocalia/big"));
		flowerWithItemModel(IncBlocks.SANVOCALIA_SMALL, Inc.id("block/sanvocalia/small"));
		flowerWithItemModel(IncBlocks.FUNNY, Inc.id("block/funny/thisissosad"));
		flowerWithItemModel(IncBlocks.FUNNY_SMALL, Inc.id("block/funny/alexaplaydespacito"));
		floatingFlowerWithItemModel(IncBlocks.FLOATING_SANVOCALIA, IncBlocks.SANVOCALIA);
		floatingFlowerWithItemModel(IncBlocks.FLOATING_SANVOCALIA_SMALL, IncBlocks.SANVOCALIA_SMALL);
		floatingFlowerWithItemModel(IncBlocks.FLOATING_FUNNY, IncBlocks.FUNNY);
		floatingFlowerWithItemModel(IncBlocks.FLOATING_FUNNY_SMALL, IncBlocks.FUNNY_SMALL);
		
		/// Unstable cubes ///
		for(Block cube : IncBlocks.UNSTABLE_CUBES.values()) {
			singleVariantBlockState(cube, Inc.id("block/unstable_cube"));
			itemBlockRotationsBuiltinEntity(cube);
		}
		
		/// Clearly ///
		singleVariantCubeColumn(IncBlocks.CLEARLY, Inc.id("block/clearly"), Inc.id("black"));
		itemBlockModelParent(IncBlocks.CLEARLY);
		
		/// Taters ///
		for(CompressedTinyPotatoBlock tater : IncBlocks.COMPRESSED_TATERS.values()) {
			ResourceLocation modelId = ModelLocationUtils.getModelLocation(tater);
			
			//Manually produce a tato model. Set the parent to the botania tiny potato model
			JsonObject json = new JsonObject();
			json.addProperty("parent", ModelLocationUtils.getModelLocation(ModBlocks.tinyPotato).toString());
			
			//Customize the transforms to make it bigger or smaller :)
			ItemTransforms transforms = ItemTransformUtil.scaleItemTransforms(ItemTransformUtil.BLOCK_BLOCK, CompressedTaterUtil.taterScaleFactor(tater.compressionLevel));
			json.add("display", ItemTransformUtil.toJson(transforms));
			
			//Write the model and a blockstate pointing to the model
			modelOutput.accept(modelId, () -> json);
			stateGenerators.add(MultiVariantGenerator.multiVariant(tater, modelv(modelId))
				.with(AccessorBlockModelGenerators.horizontalDispatch()));
			//Write an item model pointing at it
			itemDelegatedTo(tater, modelId);
		}
		
		/// Computer ///
		
		/// Aaaand write out all the files now ///
		stateGenerators.forEach(stateGenerator -> {
			ResourceLocation id = DataDsl.notAir(Registry.BLOCK.getKey(stateGenerator.getBlock()));
			files.accept(JsonFile.create(stateGenerator.get(), "assets", id.getNamespace(), "blockstates", id.getPath()));
		});
		
		models.forEach((id, elementSupplier) ->
			files.accept(JsonFile.create(elementSupplier.get(), "assets", id.getNamespace(), "models", id.getPath())));
	}
	
	/// utilities that write out a block model, and a blockstate file that always points at the model ///
	
	public static void singleVariantBlockState(Block b, ResourceLocation model) {
		stateGenerators.add(MultiVariantGenerator.multiVariant(b, Variant.variant().with(VariantProperties.MODEL, model)));
	}
	
	public static void singleVariantParticleOnly(Block b, ResourceLocation texture) {
		singleVariantBlockState(b, ModelTemplates.PARTICLE_ONLY.create(b, TextureMapping.particle(texture), modelOutput));
	}
	
	public static void singleVariantCubeAll(Block b, ResourceLocation texture) {
		singleVariantBlockState(b, ModelTemplates.CUBE_ALL.create(b, TextureMapping.cube(texture), modelOutput));
	}
	
	public static void singleVariantCubeColumn(Block b, ResourceLocation side, ResourceLocation end) {
		singleVariantBlockState(b, ModelTemplates.CUBE_COLUMN.create(b, TextureMapping.column(side, end), modelOutput));
	}
	
	public static final ModelTemplate threeHighBottomTopTemplate = template(Inc.botaniaId("block/shapes/three_high_bottom_top"), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
	public static void singleVariantThreeHighBottomTop(Block b, ResourceLocation bottom, ResourceLocation top, ResourceLocation side) {
		singleVariantBlockState(b, threeHighBottomTopTemplate.create(b, txmap(TextureSlot.BOTTOM, bottom, TextureSlot.TOP, top).put(TextureSlot.SIDE, side), modelOutput));
	}
	
	/// generates a flower, and also generates an item model to go with it ///
	
	public static final ModelTemplate crossTemplate = template(Inc.botaniaId("block/shapes/cross"), TextureSlot.CROSS);
	public static void flowerWithItemModel(Block b, ResourceLocation texture) {
		singleVariantBlockState(b, crossTemplate.create(b, TextureMapping.cross(texture), modelOutput));
		itemGenerated(b, texture);
	}
	
	public static void floatingFlowerWithItemModel(Block floating, Block nonFloating) {
		//brittle but might work
		ResourceLocation floatingModelId = DataDsl.prefixPath(Registry.BLOCK.getKey(floating), "block");
		ResourceLocation nonFloatingModelId = DataDsl.prefixPath(Registry.BLOCK.getKey(nonFloating), "block");
		
		//construct the model json manually (minecraft utils don't have any support for "loader")
		JsonObject json = new JsonObject();
		json.addProperty("parent", "minecraft:block/block");
		json.addProperty("loader", "botania:floating_flower");
		
		JsonObject flower = new JsonObject();
		flower.addProperty("parent", nonFloatingModelId.toString());
		json.add("flower", flower);
		
		//output it with the blockmodels
		modelOutput.accept(floatingModelId, () -> json);
		//add a blockstate pointing at it
		singleVariantBlockState(floating, floatingModelId);
		//and add an item model pointing at the block model
		itemBlockModelParent(floating);
	}
	
	/// more complicated blockstates ///
	
	public static void redString(Block block, ResourceLocation texture) {
		ResourceLocation modelId = ModelTemplates.CUBE_ORIENTABLE.create(block, txmap(
			TextureSlot.TOP, texture,
			TextureSlot.SIDE, texture,
			TextureSlot.FRONT, Inc.botaniaId("block/red_string_sender")
		), modelOutput);
		
		stateGenerators.add(MultiVariantGenerator.multiVariant(block, modelv(modelId)).with(AccessorBlockModelGenerators.facingDispatch()));
	}
	
	public static final TextureSlot TX_TORCH = txslot("torch");
	public static final ModelTemplate naturalRepeater = template(Inc.id("block/natural_devices/natural_repeater"), TextureSlot.TOP, TX_TORCH);
	public static ResourceLocation naturalRepeaterModel(boolean lit) {
		ResourceLocation modelId = Inc.id("block/natural_devices/natural_repeater_" + (lit ? "lit" : "unlit"));
		ResourceLocation topTexture = Inc.id("block/natural_devices/top/repeater_" + (lit ? "lit" : "unlit"));
		
		return naturalRepeater.create(modelId, txmap(
			TextureSlot.TOP, topTexture,
			TX_TORCH, naturalDeviceTorchTexture(lit)
		), modelOutput);
	}
	
	public static final TextureSlot TX_TORCH_FRONT = txslot("torch_front");
	public static final TextureSlot TX_TORCH_BACK = txslot("torch_back");
	public static final ModelTemplate naturalComparator = template(Inc.id("block/natural_devices/natural_comparator"), TextureSlot.TOP, TX_TORCH_FRONT, TX_TORCH_BACK);
	public static ResourceLocation naturalComparatorModel(boolean lit, boolean sensitive) {
		ResourceLocation modelId = Inc.id("block/natural_devices/natural_comparator_" + (sensitive ? "sensitive_" : "") + (lit ? "lit" : "unlit"));
		ResourceLocation topTexture = Inc.id("block/natural_devices/top/comparator_" + (lit ? "lit" : "unlit"));
		
		return naturalComparator.create(modelId, txmap(
			TextureSlot.TOP, topTexture,
			TX_TORCH_FRONT, naturalDeviceTorchTexture(sensitive),
			TX_TORCH_BACK, naturalDeviceTorchTexture(lit)
		), modelOutput);
	}
	
	public static ResourceLocation naturalDeviceTorchTexture(boolean torchLit) {
		return Inc.id("block/natural_devices/torch_" + (torchLit ? "lit" : "unlit"));
	}
	
	/// item models ///
	
	public static void itemBlockModelParent(Block block) {
		itemBlockModelParent(block, block);
	}
	
	public static void itemBlockModelParent(ItemLike item, Block blockParent) {
		itemDelegatedTo(item, ModelLocationUtils.getModelLocation(blockParent));
	}
	
	public static void itemBlockRotationsBuiltinEntity(ItemLike item) {
		//hand-written json that contains the rotations from minecraft:block/block, but inherits from block:builtin/entity
		itemDelegatedTo(item, Inc.id("item/block_rotations_builtin_entity"));
	}
	
	public static void itemDelegatedTo(ItemLike item, ResourceLocation delegated) {
		modelOutput.accept(ModelLocationUtils.getModelLocation(item.asItem()), new DelegatedModel(delegated));
	}
	
	public static void itemGenerated(ItemLike b, ResourceLocation texture) {
		ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(b.asItem()), TextureMapping.layer0(texture), modelOutput);
	}
	
	/// To save typing ///
	
	public static Variant modelv(ResourceLocation id) {
		return Variant.variant().with(VariantProperties.MODEL, id);
	}
	
	public static TextureSlot txslot(String name) {
		return TextureSlotAccessor.inc$create(name);
	}
	
	//Alternate TextureSlot and ResourceLocation arguments
	public static TextureMapping txmap(Object... inputs) {
		TextureMapping mapping = new TextureMapping();
		
		Iterator<Object> objerator = List.of(inputs).iterator();
		while(objerator.hasNext()) {
			TextureSlot slot = (TextureSlot) objerator.next();
			ResourceLocation texture = (ResourceLocation) objerator.next(); 
			mapping = mapping.put(slot, texture);
		}
		
		return mapping;
	}
	
	public static ModelTemplate template(ResourceLocation id, TextureSlot... slots) {
		return new ModelTemplate(Optional.of(id), Optional.empty(), slots);
	}
}
