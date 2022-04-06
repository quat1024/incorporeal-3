package agency.highlysuspect.incorporeal.mixin.hacky;

import agency.highlysuspect.incorporeal.Inc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.handler.EquipmentHandler;

@Mixin(EquipmentHandler.class)
public class EquipmentHandlerMixin {
	@Unique private static boolean hasBeenInitialized = false;
	
	@Inject(method = "init", at = @At("HEAD"), remap = false, cancellable = true)
	private static void onInit(CallbackInfo ci) {
		Inc.LOGGER.info("SOMEONE CALLED EQUIPMENTHANDLER INIT!!!!!!!!!!!");
		new Throwable().printStackTrace();
		
		if(hasBeenInitialized) ci.cancel();
		hasBeenInitialized = true;
	}
}
