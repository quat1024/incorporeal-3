package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.util.CompressedTaterUtil;
import agency.highlysuspect.incorporeal.block.CompressedTinyPotatoBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.render.tile.RenderTileTinyPotato;
import vazkii.botania.common.block.tile.TileTinyPotato;

import javax.annotation.Nonnull;

@Mixin(RenderTileTinyPotato.class)
public class RenderTileTinyPotatoMixin {
	@Inject(
		method = "render(Lvazkii/botania/common/block/tile/TileTinyPotato;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
			shift = At.Shift.AFTER,
			ordinal = 0
		)
	)
	public void afterTranslating(@Nonnull TileTinyPotato potato, float partialTicks, PoseStack ms, @Nonnull MultiBufferSource buffers, int light, int overlay, CallbackInfo ci) {
		BlockState state = potato.getBlockState();
		if(state.getBlock() instanceof CompressedTinyPotatoBlock compressed) {
			float scaleFactor = CompressedTaterUtil.taterScaleFactor(compressed.compressionLevel);
			ms.scale(scaleFactor, scaleFactor, scaleFactor);
		}
	}
}
