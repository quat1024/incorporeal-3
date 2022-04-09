package agency.highlysuspect.incorporeal.mixin.client;

import agency.highlysuspect.incorporeal.client.TicketConjurerHudHandler;
import agency.highlysuspect.incorporeal.client.computer.DataseerMonocleHudHandler;
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
		TicketConjurerHudHandler.botaniaDrewNearIndexDisplay = false;
	}
	
	@Inject(method = "renderNearIndexDisplay", remap = false, at = @At("HEAD"))
	private static void startRenderNearIndexDisplay(PoseStack ms, CallbackInfo ci) {
		TicketConjurerHudHandler.botaniaDrewNearIndexDisplay = true;
	}
	
	@Inject(method = "onDrawScreenPost", remap = false, at = @At("TAIL"))
	private static void endOnDrawScreenPost(PoseStack ms, float partialTicks, CallbackInfo ci) {
		TicketConjurerHudHandler.doIt(ms, partialTicks);
		DataseerMonocleHudHandler.doIt(ms, partialTicks);
	}
}
