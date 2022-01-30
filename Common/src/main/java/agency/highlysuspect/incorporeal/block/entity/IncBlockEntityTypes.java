package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.function.BiConsumer;

public class IncBlockEntityTypes {
	public static final BlockEntityType<RedStringLiarBlockEntity> RED_STRING_LIAR = IXplatAbstractions.INSTANCE.createBlockEntityType(RedStringLiarBlockEntity::new, IncBlocks.RED_STRING_LIAR);
	
	public static void register(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
		r.accept(RED_STRING_LIAR, Inc.id("red_string_liar"));
	}
}
