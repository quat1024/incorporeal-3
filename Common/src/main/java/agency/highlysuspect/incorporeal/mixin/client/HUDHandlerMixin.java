package agency.highlysuspect.incorporeal.mixin.client;

import agency.highlysuspect.incorporeal.client.IncHudHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.gui.HUDHandler;

@Mixin(HUDHandler.class)
public class HUDHandlerMixin {
	@Inject(method = "onDrawScreenPost", remap = false, at = @At("HEAD"))
	private static void startOnDrawScreenPost(PoseStack ms, float partialTicks, CallbackInfo ci) {
		IncHudHandler.botaniaDrewNearIndexDisplay = false;
	}
	
	@Inject(method = "renderNearIndexDisplay", remap = false, at = @At("HEAD"))
	private static void startRenderNearIndexDisplay(PoseStack ms, CallbackInfo ci) {
		IncHudHandler.botaniaDrewNearIndexDisplay = true;
	}
	
	@Inject(method = "onDrawScreenPost", remap = false, at = @At("TAIL"))
	private static void endOnDrawScreenPost(PoseStack ms, float partialTicks, CallbackInfo ci) {
		IncHudHandler.doIt(ms, partialTicks);
	}
}
