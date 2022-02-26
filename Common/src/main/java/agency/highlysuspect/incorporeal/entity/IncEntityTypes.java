package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.Inc;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiConsumer;

public class IncEntityTypes {
	public static final EntityType<FracturedSpaceCollector> FRACTURED_SPACE_COLLECTOR =
		EntityType.Builder.<FracturedSpaceCollector>of(FracturedSpaceCollector::new, MobCategory.MISC)
			.sized(4f, 0.1f)
			.fireImmune()
			.updateInterval(Integer.MAX_VALUE)
			.clientTrackingRange(10)
			.build(Inc.id("fractured_space_collector").toString()); //this string doesn't appear to actually be used, except for complaining about datafixers
	
	public static void register(BiConsumer<EntityType<?>, ResourceLocation> r) {
		r.accept(FRACTURED_SPACE_COLLECTOR, Inc.id("fractured_space_collector"));
	}
}
