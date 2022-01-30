package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vazkii.botania.common.block.ModBlocks;

import java.util.function.BiConsumer;

public class IncBlocks {
	public static final CorporeaSolidifierBlock CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(BlockBehaviour.Properties.copy(ModBlocks.corporeaRetainer));
	public static final CorporeaRetainerEvaporatorBlock CORPOREA_RETAINER_EVAPORATOR = new CorporeaRetainerEvaporatorBlock(BlockBehaviour.Properties.copy(ModBlocks.corporeaRetainer));
	
	public static void register(BiConsumer<Block, ResourceLocation> r) {
		r.accept(CORPOREA_SOLIDIFIER, Inc.id("corporea_solidifier"));
		r.accept(CORPOREA_RETAINER_EVAPORATOR, Inc.id("corporea_retainer_evaporator"));
	}
}
