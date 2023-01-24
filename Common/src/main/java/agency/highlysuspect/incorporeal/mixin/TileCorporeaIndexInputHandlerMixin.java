package agency.highlysuspect.incorporeal.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;

@Mixin(CorporeaIndexBlockEntity.class)
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
	private static void whenChatMessage(ServerPlayer player, String message, CallbackInfo ci) {
		/*if(TicketConjurerItem.handleChatMessage(player, message)) {
			//cancelling at all: prevents Corporea Index code from handling it
			//cancelling with specifically "true": botania machinery prevents the message from being sent to chat
			// TODO this changed in 1.19, investigate
			ci.cancel();
		}*/
	}
}
