package agency.highlysuspect.incorporeal.platform.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * The named DamageSource constructor is private on Fabric.
 */
@Mixin(DamageSource.class)
public interface FabricAccessorDamageSource {
	@Invoker("<init>")
	static DamageSource inc$new(String name) {
		throw new AssertionError("mixin failed to apply"); 
	}
}
