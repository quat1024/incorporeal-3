package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.SoulCoreBlock;
import agency.highlysuspect.incorporeal.entity.FracturedSpaceCollector;
import agency.highlysuspect.incorporeal.entity.PotionSoulCoreCollector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.BiConsumer;

public class IncEntityTypes {
	public static final EntityType<FracturedSpaceCollector> FRACTURED_SPACE_COLLECTOR =
		EntityType.Builder.<FracturedSpaceCollector>of(FracturedSpaceCollector::new, MobCategory.MISC)
			.sized(4f, 0.1f)
			.fireImmune()
			.updateInterval(Integer.MAX_VALUE)
			.clientTrackingRange(10)
			.build("incorporeal:fractured_space_collector"); //this string doesn't appear to actually be used, except for complaining about datafixers
	
	public static final EntityType<PotionSoulCoreCollector> POTION_SOUL_CORE_COLLECTOR =
		EntityType.Builder.<PotionSoulCoreCollector>of(PotionSoulCoreCollector::new, MobCategory.MISC)
			.sized(PotionSoulCoreCollector.WIDTH, PotionSoulCoreCollector.HEIGHT)
			.fireImmune()
			.updateInterval(40)
			.clientTrackingRange(10)
			.build("incorporeal:potion_soul_core_collector");
	
	public static void register(BiConsumer<EntityType<?>, ResourceLocation> r) {
		r.accept(FRACTURED_SPACE_COLLECTOR, Inc.id("fractured_space_collector"));
		r.accept(POTION_SOUL_CORE_COLLECTOR, Inc.id("potion_soul_core_collector"));
	}
	
	public static void registerAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> r) {
		r.accept(POTION_SOUL_CORE_COLLECTOR, PotionSoulCoreCollector.attrs());
	}
}
