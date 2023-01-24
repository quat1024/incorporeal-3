package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.entity.CorporeaPylonBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.FunnyBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.PotionSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.SanvocaliaBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.UnstableCubeBlockEntity;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class IncBlockEntityTypes {
	private static <T extends BlockEntity> BlockEntityType<T> make(BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
		return XplatAbstractions.INSTANCE.createBlockEntityType(func, blocks);
	}
	
	private static <T extends BlockEntity> BlockEntityType<T> make(BiFunction<BlockPos, BlockState, T> func, Collection<? extends Block> blocks) {
		return XplatAbstractions.INSTANCE.createBlockEntityType(func, blocks.toArray(Block[]::new));
	}
	
	//corporetics
	public static final BlockEntityType<RedStringLiarBlockEntity> RED_STRING_LIAR = make(IncXplat.INSTANCE.redStringLiarMaker()::create, IncBlocks.RED_STRING_LIAR);
	public static final BlockEntityType<RedStringConstrictorBlockEntity> RED_STRING_CONSTRICTOR = make(IncXplat.INSTANCE.redStringConstrictorMaker()::create, IncBlocks.RED_STRING_CONSTRICTOR);
	public static final BlockEntityType<CorporeaPylonBlockEntity> CORPOREA_PYLON = make(CorporeaPylonBlockEntity::new, IncBlocks.CORPOREA_PYLON);
	
	//soul cores
	public static final BlockEntityType<EnderSoulCoreBlockEntity> ENDER_SOUL_CORE = make(EnderSoulCoreBlockEntity::new, IncBlocks.ENDER_SOUL_CORE);
	public static final BlockEntityType<PotionSoulCoreBlockEntity> POTION_SOUL_CORE = make(PotionSoulCoreBlockEntity::new, IncBlocks.POTION_SOUL_CORE);
	
	//flowers
	public static final BlockEntityType<SanvocaliaBlockEntity> SANVOCALIA_BIG = make(SanvocaliaBlockEntity::big, IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA);
	public static final BlockEntityType<SanvocaliaBlockEntity> SANVOCALIA_SMALL = make(SanvocaliaBlockEntity::small, IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA_SMALL);
	public static final BlockEntityType<FunnyBlockEntity> FUNNY_BIG = make(FunnyBlockEntity::big, IncBlocks.FUNNY, IncBlocks.FLOATING_FUNNY);
	public static final BlockEntityType<FunnyBlockEntity> FUNNY_SMALL = make(FunnyBlockEntity::small, IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY_SMALL);
	
	//unstable cubes
	public static final BlockEntityType<UnstableCubeBlockEntity> UNSTABLE_CUBE = make(UnstableCubeBlockEntity::new, IncBlocks.UNSTABLE_CUBES.values());

	//BlockEntityTypes that self-implement IManaReceiver.
	public static final Set<BlockEntityType<? extends ManaReceiver>> SELF_MANA_RECEIVER_BLOCK_ENTITY_TYPES = Set.of(
		ENDER_SOUL_CORE, POTION_SOUL_CORE
	);
	
	public static final Set<BlockEntityType<? extends Wandable>> SELF_WANDABLE_BLOCK_ENTITY_TYPES = Set.of(
		UNSTABLE_CUBE
	);
	
	public static void register(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
		r.accept(RED_STRING_LIAR, Inc.id("red_string_liar"));
		r.accept(RED_STRING_CONSTRICTOR, Inc.id("red_string_constrictor"));
		r.accept(CORPOREA_PYLON, Inc.id("corporea_pylon"));
		
		r.accept(ENDER_SOUL_CORE, Inc.id("ender_soul_core"));
		r.accept(POTION_SOUL_CORE, Inc.id("potion_soul_core"));
		
		r.accept(SANVOCALIA_BIG, Inc.id("sanvocalia"));
		r.accept(SANVOCALIA_SMALL, Inc.id("sanvocalia_small"));
		r.accept(FUNNY_BIG, Inc.id("funny"));
		r.accept(FUNNY_SMALL, Inc.id("funny_small"));
		
		r.accept(UNSTABLE_CUBE, Inc.id("unstable_cube"));
	}
}
