package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import vazkii.botania.common.block.ModBlocks;

import java.util.function.BiConsumer;

public class IncBlocks {
	public static final CorporeaSolidifierBlock CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(Properties.copy(ModBlocks.corporeaRetainer));
	public static final RedStringLiarBlock RED_STRING_LIAR = new RedStringLiarBlock(Properties.copy(ModBlocks.livingrock));
	public static final FrameTinkererBlock FRAME_TINKERER = new FrameTinkererBlock(Properties.copy(Blocks.OAK_PLANKS));
	public static final CorporeaRetainerEvaporatorBlock CORPOREA_RETAINER_EVAPORATOR = new CorporeaRetainerEvaporatorBlock(Properties.copy(ModBlocks.corporeaRetainer));
	
	public static void register(BiConsumer<Block, ResourceLocation> r) {
		r.accept(CORPOREA_SOLIDIFIER, Inc.id("corporea_solidifier"));
		r.accept(RED_STRING_LIAR, Inc.id("red_string_liar"));
		r.accept(FRAME_TINKERER, Inc.id("frame_tinkerer"));
		r.accept(CORPOREA_RETAINER_EVAPORATOR, Inc.id("corporea_retainer_evaporator"));
	}
}
