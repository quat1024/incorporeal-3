package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.Tupling;
import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.computer.DataLens;
import agency.highlysuspect.incorporeal.computer.DataPrismBlock;
import agency.highlysuspect.incorporeal.computer.DataStorageBlock;
import agency.highlysuspect.incorporeal.computer.LensBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import vazkii.botania.common.block.BlockFloatingSpecialFlower;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModSubtiles;

import java.util.Map;
import java.util.function.BiConsumer;

public class IncBlocks {
	//corporetics
	public static final CorporeaSolidifierBlock CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(Properties.copy(ModBlocks.corporeaRetainer));
	public static final RedStringLiarBlock RED_STRING_LIAR = new RedStringLiarBlock(Properties.copy(ModBlocks.livingrock));
	public static final FrameTinkererBlock FRAME_TINKERER = new FrameTinkererBlock(Properties.copy(Blocks.OAK_PLANKS));
	
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
	private static final Properties flowerProps = Properties.copy(ModSubtiles.agricarnation);
	private static final Properties floatingProps = Properties.copy(ModSubtiles.agricarnationFloating);
	
	public static final BlockSpecialFlower SANVOCALIA = new BlockSpecialFlower(MobEffects.GLOWING, 20, flowerProps, () -> IncBlockEntityTypes.SANVOCALIA_BIG);
	public static final BlockSpecialFlower SANVOCALIA_SMALL = new BlockSpecialFlower(MobEffects.GLOWING, 5, flowerProps, () -> IncBlockEntityTypes.SANVOCALIA_SMALL);
	public static final BlockFloatingSpecialFlower FLOATING_SANVOCALIA = new BlockFloatingSpecialFlower(floatingProps, () -> IncBlockEntityTypes.SANVOCALIA_BIG);
	public static final BlockFloatingSpecialFlower FLOATING_SANVOCALIA_SMALL = new BlockFloatingSpecialFlower(floatingProps, () -> IncBlockEntityTypes.SANVOCALIA_SMALL);
	
	//I am aware that this sussy stew potion effect is a terrible idea, but i'm doing it anyway because it's funny as hell.
	public static final BlockSpecialFlower FUNNY = new BlockSpecialFlower(MobEffects.BAD_OMEN, 20, flowerProps, () -> IncBlockEntityTypes.FUNNY_BIG);
	public static final BlockSpecialFlower FUNNY_SMALL = new BlockSpecialFlower(MobEffects.BAD_OMEN, 5, flowerProps, () -> IncBlockEntityTypes.FUNNY_SMALL);
	public static final BlockFloatingSpecialFlower FLOATING_FUNNY = new BlockFloatingSpecialFlower(floatingProps, () -> IncBlockEntityTypes.FUNNY_BIG);
	public static final BlockFloatingSpecialFlower FLOATING_FUNNY_SMALL = new BlockFloatingSpecialFlower(floatingProps, () -> IncBlockEntityTypes.FUNNY_SMALL);
	
	//unstable cubes
	public static final Map<DyeColor, UnstableCubeBlock> UNSTABLE_CUBES = Inc.sixteenColors(color ->
		new UnstableCubeBlock(color, Properties.of(Material.METAL, color.getMaterialColor())
			.strength(5f)
			.noOcclusion()));
	
	//clearly
	public static final Block CLEARLY = new ClearlyBlock(Properties.of(Material.METAL).sound(SoundType.NETHER_SPROUTS).strength(1f));
	
	//taters
	public static final Map<Tupling, CompressedTinyPotatoBlock> COMPRESSED_TATERS = Inc.octupleCompressed(tupling ->
		new CompressedTinyPotatoBlock(tupling, Properties.copy(ModBlocks.tinyPotato)
			.noOcclusion() //octuple tater is a full-block square on the bottom and it culls stuff lmao
			.strength(tupling.level() / 2f)));
	
	//computer
	public static final DataPrismBlock DATA_PRISM = new DataPrismBlock(Properties.of(Material.METAL).noOcclusion());
	public static final DataStorageBlock DATA_STORAGE = new DataStorageBlock(Properties.of(Material.METAL).noOcclusion());
	public static final LensBlock MATCHER_LENS = new LensBlock(DataLens.matcher, Properties.of(Material.GLASS).noOcclusion());
	public static final LensBlock NUMBER_LENS = new LensBlock(DataLens.number, Properties.of(Material.GLASS).noOcclusion());
	public static final LensBlock NEGATORY_LENS = new LensBlock(DataLens.negatory, Properties.of(Material.GLASS).noOcclusion());
	
	public static void register(BiConsumer<Block, ResourceLocation> r) {
		//assorted corporea devices
		r.accept(CORPOREA_SOLIDIFIER, Inc.id("corporea_solidifier"));
		r.accept(RED_STRING_LIAR, Inc.id("red_string_liar"));
		r.accept(FRAME_TINKERER, Inc.id("frame_tinkerer"));
		
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
		
		//taters
		COMPRESSED_TATERS.forEach((tupling, block) -> r.accept(block, Inc.id(tupling.compressedPrefix("tiny_potato"))));
		
		//computer
		r.accept(DATA_PRISM, Inc.id("data_prism"));
		r.accept(DATA_STORAGE, Inc.id("data_storage"));
		r.accept(MATCHER_LENS, Inc.id("matcher_lens"));
		r.accept(NUMBER_LENS, Inc.id("number_lens"));
		r.accept(NEGATORY_LENS, Inc.id("negatory_lens"));
	}
}
