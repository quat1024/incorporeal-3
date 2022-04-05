package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

@Mixin(TileCorporeaIndex.InputHandler.class)
public class TileCorporeaIndexInputHandlerMixin {
	/**
	 * Instead of making my own multi-loader abstraction for handling chat messages i will simply piggy back off Botania's :))
	 */
	@Inject(
		method = "onChatMessage",
		remap = false,
		at = @At("HEAD"),
		cancellable = true
	)
	private void whenChatMessage(ServerPlayer player, String message, CallbackInfoReturnable<Boolean> cir) {
		if(TicketConjurerItem.handleChatMessage(player, message)) {
			//cancelling at all: prevents Corporea Index code from handling it
			//cancelling with specifically "true": botania machinery prevents the message from being sent to chat
			cir.setReturnValue(true);
		}
	}
}
