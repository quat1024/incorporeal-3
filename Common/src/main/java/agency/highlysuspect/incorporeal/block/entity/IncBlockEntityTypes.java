package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.computer.DataFunnelBlockEntity;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.Map;
import java.util.function.BiConsumer;

public class IncBlockEntityTypes {
	//corporetics
	public static final BlockEntityType<RedStringLiarBlockEntity> RED_STRING_LIAR =
		IXplatAbstractions.INSTANCE.createBlockEntityType(IncXplat.INSTANCE.redStringLiarMaker()::create, IncBlocks.RED_STRING_LIAR);
	public static final BlockEntityType<RedStringConstrictorBlockEntity> RED_STRING_CONSTRICTOR =
		IXplatAbstractions.INSTANCE.createBlockEntityType(IncXplat.INSTANCE.redStringConstrictorMaker()::create, IncBlocks.RED_STRING_CONSTRICTOR);
	
	//soul cores
	public static final BlockEntityType<EnderSoulCoreBlockEntity> ENDER_SOUL_CORE = 
		IXplatAbstractions.INSTANCE.createBlockEntityType(EnderSoulCoreBlockEntity::new, IncBlocks.ENDER_SOUL_CORE);
	public static final BlockEntityType<PotionSoulCoreBlockEntity> POTION_SOUL_CORE =
		IXplatAbstractions.INSTANCE.createBlockEntityType(PotionSoulCoreBlockEntity::new, IncBlocks.POTION_SOUL_CORE);
	
	//flowers
	public static final BlockEntityType<SanvocaliaBlockEntity> SANVOCALIA_BIG =
		IXplatAbstractions.INSTANCE.createBlockEntityType(SanvocaliaBlockEntity::big, IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA);
	public static final BlockEntityType<SanvocaliaBlockEntity> SANVOCALIA_SMALL =
		IXplatAbstractions.INSTANCE.createBlockEntityType(SanvocaliaBlockEntity::small, IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA_SMALL);
	
	public static final BlockEntityType<FunnyBlockEntity> FUNNY_BIG =
		IXplatAbstractions.INSTANCE.createBlockEntityType(FunnyBlockEntity::big, IncBlocks.FUNNY, IncBlocks.FLOATING_FUNNY);
	public static final BlockEntityType<FunnyBlockEntity> FUNNY_SMALL =
		IXplatAbstractions.INSTANCE.createBlockEntityType(FunnyBlockEntity::small, IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY_SMALL);
	
	//unstable cubes
	//Note - Only one BlockEntityRenderer can be assigned per block entity type.
	public static final Map<DyeColor, BlockEntityType<UnstableCubeBlockEntity>> UNSTABLE_CUBES = Inc.sixteenColors(color ->
		IXplatAbstractions.INSTANCE.createBlockEntityType(
			(pos, state) -> new UnstableCubeBlockEntity(color, pos, state), 
			IncBlocks.UNSTABLE_CUBES.get(color)));
	
	//computer
	public static final BlockEntityType<DataFunnelBlockEntity> DATA_FUNNEL =
		IXplatAbstractions.INSTANCE.createBlockEntityType(DataFunnelBlockEntity::new, IncBlocks.DATA_FUNNEL);
	
	public static void register(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
		r.accept(RED_STRING_LIAR, Inc.id("red_string_liar"));
		r.accept(RED_STRING_CONSTRICTOR, Inc.id("red_string_constrictor"));
		
		r.accept(ENDER_SOUL_CORE, Inc.id("ender_soul_core"));
		r.accept(POTION_SOUL_CORE, Inc.id("potion_soul_core"));
		
		r.accept(SANVOCALIA_BIG, Inc.id("sanvocalia"));
		r.accept(SANVOCALIA_SMALL, Inc.id("sanvocalia_small"));
		r.accept(FUNNY_BIG, Inc.id("funny"));
		r.accept(FUNNY_SMALL, Inc.id("funny_small"));
		
		UNSTABLE_CUBES.forEach((color, type) -> r.accept(type, Inc.id(color.getName() + "_unstable_cube")));
		
		r.accept(DATA_FUNNEL, Inc.id("data_funnel"));
	}
}
