package agency.highlysuspect.incorporeal.platform.fabric.mixin;

import agency.highlysuspect.incorporeal.platform.fabric.IncFabric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.fabric.FabricCommonInitializer;

/**
 * Don't look at me like that, it's because the class I mixin to starts with Fabric
 * but I also prefix all loader-specific mixins with the loader name
 * 
 * Anyway, here's what happens when you write a modloader with no defined load order
 */
@Mixin(FabricCommonInitializer.class)
public class FabricFabricCommonInitializerMixin {
	@Inject(
		method = "onInitialize",
		at = @At("RETURN"),
		remap = false
	)
	private void afterInitialize(CallbackInfo ci) {
		IncFabric.botaniaInit = true;
		IncFabric.afterBotania();
	}
}
