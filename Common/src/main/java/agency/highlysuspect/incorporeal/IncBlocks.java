package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.ClearlyBlock;
import agency.highlysuspect.incorporeal.block.CompressedTinyPotatoBlock;
import agency.highlysuspect.incorporeal.block.CorporeaPylonBlock;
import agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock;
import agency.highlysuspect.incorporeal.block.CrappyComparatorBlock;
import agency.highlysuspect.incorporeal.block.CrappyRepeaterBlock;
import agency.highlysuspect.incorporeal.block.FrameTinkererBlock;
import agency.highlysuspect.incorporeal.block.RedStringConstrictorBlock;
import agency.highlysuspect.incorporeal.block.RedStringLiarBlock;
import agency.highlysuspect.incorporeal.block.RedstoneRootCropBlock;
import agency.highlysuspect.incorporeal.block.SoulCoreBlock;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.block.PointedDatastoneBlock;
import agency.highlysuspect.incorporeal.block.DatastoneBlock;
import agency.highlysuspect.incorporeal.util.CompressedTaterUtil;
import agency.highlysuspect.incorporeal.block.DataFunnelBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.botania.common.block.BlockFloatingSpecialFlower;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModSubtiles;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class IncBlocks {
	//corporetics
	public static final CorporeaSolidifierBlock CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(Properties.copy(ModBlocks.corporeaRetainer));
	public static final RedStringLiarBlock RED_STRING_LIAR = new RedStringLiarBlock(Properties.copy(ModBlocks.livingrock));
	public static final RedStringConstrictorBlock RED_STRING_CONSTRICTOR = new RedStringConstrictorBlock(Properties.copy(ModBlocks.livingrock));
	public static final FrameTinkererBlock FRAME_TINKERER = new FrameTinkererBlock(Properties.copy(Blocks.OAK_PLANKS));
	public static final CorporeaPylonBlock CORPOREA_PYLON = new CorporeaPylonBlock(Properties.copy(ModBlocks.corporeaBlock).strength(3f));
	
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
	public static final Map<Integer, CompressedTinyPotatoBlock> COMPRESSED_TATERS = new LinkedHashMap<>();
	static {
		CompressedTaterUtil.prefixes.keySet().forEach(level -> {
			float strength = Mth.clamp(level / 2f, 0.3f, 4f);
			COMPRESSED_TATERS.put(level, new CompressedTinyPotatoBlock(level, Properties.copy(ModBlocks.tinyPotato)
				.noOcclusion()
				.strength(strength)));
		});
	}
	
	//computer
	public static final DataFunnelBlock DATA_FUNNEL = new DataFunnelBlock(Properties.of(Material.METAL).noOcclusion().strength(2f));
	//similar to DRIPSTONE_BLOCK
	public static final DatastoneBlock DATASTONE_BLOCK = new DatastoneBlock(Properties.of(Material.STONE, MaterialColor.QUARTZ).sound(SoundType.DRIPSTONE_BLOCK).strength(1.5F, 1.0F).noOcclusion());
	//similar to POINTED_DRIPSTONE
	public static final PointedDatastoneBlock POINTED_DATASTONE = new PointedDatastoneBlock(Properties.of(Material.STONE, MaterialColor.QUARTZ).noOcclusion().sound(SoundType.POINTED_DRIPSTONE).strength(1.5F, 3.0F).dynamicShape());
	
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
		
		//taters
		COMPRESSED_TATERS.forEach((level, block) -> r.accept(block, Inc.id(CompressedTaterUtil.prefix(level))));
		
		//computer
		r.accept(DATA_FUNNEL, Inc.id("data_funnel"));
		r.accept(DATASTONE_BLOCK, Inc.id("datastone_block"));
		r.accept(POINTED_DATASTONE, Inc.id("pointed_datastone"));
	}
}
