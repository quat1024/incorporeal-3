package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.Map;
import java.util.function.BiConsumer;

public class IncBlockEntityTypes {
	public static final BlockEntityType<RedStringLiarBlockEntity> RED_STRING_LIAR =
		IXplatAbstractions.INSTANCE.createBlockEntityType(RedStringLiarBlockEntity::new, IncBlocks.RED_STRING_LIAR);
	
	//Note - Only one BlockEntityRenderer can be assigned per block entity type.
	public static final Map<DyeColor, BlockEntityType<UnstableCubeBlockEntity>> UNSTABLE_CUBES = Inc.sixteenColors(color ->
		IXplatAbstractions.INSTANCE.createBlockEntityType(
			(pos, state) -> new UnstableCubeBlockEntity(color, pos, state), 
			IncBlocks.UNSTABLE_CUBES.get(color)));
	
	public static void register(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
		r.accept(RED_STRING_LIAR, Inc.id("red_string_liar"));
		
		UNSTABLE_CUBES.forEach((color, type) -> r.accept(type, Inc.id(color.getName() + "_unstable_cube")));
	}
}
