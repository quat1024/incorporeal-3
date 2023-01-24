package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.ClearlyBlock;
import agency.highlysuspect.incorporeal.block.CompressedTinyPotatoBlock;
import agency.highlysuspect.incorporeal.block.CorporeaPylonBlock;
import agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock;
import agency.highlysuspect.incorporeal.block.CrappyComparatorBlock;
import agency.highlysuspect.incorporeal.block.CrappyRepeaterBlock;
import agency.highlysuspect.incorporeal.block.FrameTinkererBlock;
import agency.highlysuspect.incorporeal.block.PetalCarpetBlock;
import agency.highlysuspect.incorporeal.block.RedStringConstrictorBlock;
import agency.highlysuspect.incorporeal.block.RedStringLiarBlock;
import agency.highlysuspect.incorporeal.block.RedstoneRootCropBlock;
import agency.highlysuspect.incorporeal.block.SoulCoreBlock;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.util.CompressedTaterUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.common.block.*;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.FloatingSpecialFlowerBlock;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class IncBlocks {
	//corporetics
	public static final CorporeaSolidifierBlock CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(Properties.copy(BotaniaBlocks.corporeaRetainer));
	public static final RedStringLiarBlock RED_STRING_LIAR = new RedStringLiarBlock(Properties.copy(BotaniaBlocks.livingrock));
	public static final RedStringConstrictorBlock RED_STRING_CONSTRICTOR = new RedStringConstrictorBlock(Properties.copy(BotaniaBlocks.livingrock));
	public static final FrameTinkererBlock FRAME_TINKERER = new FrameTinkererBlock(Properties.copy(Blocks.OAK_PLANKS));
	public static final CorporeaPylonBlock CORPOREA_PYLON = new CorporeaPylonBlock(Properties.copy(BotaniaBlocks.corporeaBlock).strength(3f));
	
	//soul cores
	private static final Properties soulCoreProps = Properties.of(Material.NETHER_WOOD).sound(SoundType.NETHER_SPROUTS).strength(1f).isSuffocating((__, ___, ____) -> false).noOcclusion();
	public static final SoulCoreBlock ENDER_SOUL_CORE = new SoulCoreBlock(() -> IncBlockEntityTypes.ENDER_SOUL_CORE, soulCoreProps);
	public static final SoulCoreBlock POTION_SOUL_CORE = new SoulCoreBlock(() -> IncBlockEntityTypes.POTION_SOUL_CORE, soulCoreProps);
	
	//natural devices
	private static final Properties naturalDeviceProps = Properties.of(Material.DECORATION).instabreak().sound(SoundType.CROP).noOcclusion();
	public static final CrappyRepeaterBlock NATURAL_REPEATER = new CrappyRepeaterBlock(naturalDeviceProps);
	public static final CrappyComparatorBlock NATURAL_COMPARATOR = new CrappyComparatorBlock(naturalDeviceProps);
	public static final RedstoneRootCropBlock REDSTONE_ROOT_CROP = new RedstoneRootCropBlock(Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP));
	
	//flowers
	private static final Properties flowerProps = Properties.copy(BotaniaFlowerBlocks.agricarnation);
	private static final Properties floatingProps = Properties.copy(BotaniaFlowerBlocks.agricarnationFloating);
	
	public static final FlowerBlock SANVOCALIA = createSpecialFlowerBlock(MobEffects.GLOWING, 20, flowerProps, () -> IncBlockEntityTypes.SANVOCALIA_BIG);
	public static final FlowerBlock SANVOCALIA_SMALL = createSpecialFlowerBlock(MobEffects.GLOWING, 5, flowerProps, () -> IncBlockEntityTypes.SANVOCALIA_SMALL);
	public static final FloatingSpecialFlowerBlock FLOATING_SANVOCALIA = new FloatingSpecialFlowerBlock(floatingProps, () -> IncBlockEntityTypes.SANVOCALIA_BIG);
	public static final FloatingSpecialFlowerBlock FLOATING_SANVOCALIA_SMALL = new FloatingSpecialFlowerBlock(floatingProps, () -> IncBlockEntityTypes.SANVOCALIA_SMALL);
	
	//I am aware that this sussy stew potion effect is a terrible idea, but i'm doing it anyway because it's funny as hell.
	public static final FlowerBlock FUNNY = createSpecialFlowerBlock(MobEffects.BAD_OMEN, 20, flowerProps, () -> IncBlockEntityTypes.FUNNY_BIG);
	public static final FlowerBlock FUNNY_SMALL = createSpecialFlowerBlock(MobEffects.BAD_OMEN, 5, flowerProps, () -> IncBlockEntityTypes.FUNNY_SMALL);
	public static final FloatingSpecialFlowerBlock FLOATING_FUNNY = new FloatingSpecialFlowerBlock(floatingProps, () -> IncBlockEntityTypes.FUNNY_BIG);
	public static final FloatingSpecialFlowerBlock FLOATING_FUNNY_SMALL = new FloatingSpecialFlowerBlock(floatingProps, () -> IncBlockEntityTypes.FUNNY_SMALL);
	
	//unstable cubes
	public static final Map<DyeColor, UnstableCubeBlock> UNSTABLE_CUBES = Inc.sixteenColors(color ->
		new UnstableCubeBlock(color, Properties.of(Material.METAL, color.getMaterialColor())
			.strength(5f)
			.noOcclusion()));
	
	//clearly, and other silliness
	public static final Block CLEARLY = new ClearlyBlock(Properties.of(Material.METAL).sound(SoundType.NETHER_SPROUTS).strength(1f));
	public static final Map<DyeColor, PetalCarpetBlock> PETAL_CARPETS = Inc.sixteenColors(color -> 
		new PetalCarpetBlock(color, Properties.copy(getCarpet(color))));
	
	//taters
	public static final Map<Integer, CompressedTinyPotatoBlock> COMPRESSED_TATERS = new LinkedHashMap<>();
	static {
		CompressedTaterUtil.prefixes.keySet().forEach(level -> {
			float strength = Mth.clamp(level / 2f, 0.3f, 4f);
			COMPRESSED_TATERS.put(level, new CompressedTinyPotatoBlock(level, Properties.copy(BotaniaBlocks.tinyPotato)
				.noOcclusion()
				.strength(strength)));
		});
	}

	public static void register(BiConsumer<Block, ResourceLocation> r) {
		//assorted corporea devices
		r.accept(CORPOREA_SOLIDIFIER, Inc.id("corporea_solidifier"));
		r.accept(RED_STRING_LIAR, Inc.id("red_string_liar"));
		r.accept(RED_STRING_CONSTRICTOR, Inc.id("red_string_constrictor"));
		r.accept(FRAME_TINKERER, Inc.id("frame_tinkerer"));
		r.accept(CORPOREA_PYLON, Inc.id("corporea_pylon"));
		
		//soul cores
		r.accept(ENDER_SOUL_CORE, Inc.id("ender_soul_core"));
		r.accept(POTION_SOUL_CORE, Inc.id("potion_soul_core"));
		
		//natural devices
		r.accept(NATURAL_REPEATER, Inc.id("natural_repeater"));
		r.accept(NATURAL_COMPARATOR, Inc.id("natural_comparator"));
		r.accept(REDSTONE_ROOT_CROP, Inc.id("redstone_root_crop"));
		
		//flowers
		r.accept(SANVOCALIA, Inc.id("sanvocalia"));
		r.accept(SANVOCALIA_SMALL, Inc.id("sanvocalia_chibi"));
		r.accept(FLOATING_SANVOCALIA, Inc.id("floating_sanvocalia"));
		r.accept(FLOATING_SANVOCALIA_SMALL, Inc.id("floating_sanvocalia_chibi"));
		
		r.accept(FUNNY, Inc.id("funny"));
		r.accept(FUNNY_SMALL, Inc.id("funny_chibi"));
		r.accept(FLOATING_FUNNY, Inc.id("floating_funny"));
		r.accept(FLOATING_FUNNY_SMALL, Inc.id("floating_funny_chibi"));
		
		//unstable cubes
		UNSTABLE_CUBES.forEach((color, block) -> r.accept(block, Inc.id(color.getName() + "_unstable_cube")));
		
		//clearly
		r.accept(CLEARLY, Inc.id("clearly"));
		PETAL_CARPETS.forEach((color, block) -> r.accept(block, Inc.id(color.getName() + "_petal_block_carpet")));
		
		//taters
		COMPRESSED_TATERS.forEach((level, block) -> r.accept(block, Inc.id(CompressedTaterUtil.prefix(level))));
	}

	private static FlowerBlock createSpecialFlowerBlock(
			MobEffect effect, int effectDuration,
			BlockBehaviour.Properties props,
			Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> beType) {
		return XplatAbstractions.INSTANCE.createSpecialFlowerBlock(
				effect, effectDuration, props, beType
		);
	}
	
	//im sure theres a vanilla method for this hiding somewhere
	private static Block getCarpet(DyeColor color) {
		return switch(color) {
			case WHITE -> Blocks.WHITE_CARPET;
			case ORANGE -> Blocks.ORANGE_CARPET;
			case MAGENTA -> Blocks.MAGENTA_CARPET;
			case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CARPET;
			case YELLOW -> Blocks.YELLOW_CARPET;
			case LIME -> Blocks.LIME_CARPET;
			case PINK -> Blocks.PINK_CARPET;
			case GRAY -> Blocks.GRAY_CARPET;
			case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CARPET;
			case CYAN -> Blocks.CYAN_CARPET;
			case PURPLE -> Blocks.PURPLE_CARPET;
			case BLUE -> Blocks.BLUE_CARPET;
			case BROWN -> Blocks.BROWN_CARPET;
			case GREEN -> Blocks.GREEN_CARPET;
			case RED -> Blocks.RED_CARPET;
			case BLACK -> Blocks.BLACK_CARPET;
		};
	}
}
