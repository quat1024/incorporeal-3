package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.CrappyDiodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes farmland not regress to dirt underneath a Natural Repeater/Comparator block
 */
@Mixin(FarmBlock.class)
public class FarmBlockMixin {
	@Inject(
		method = "isUnderCrops",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void whenCheckingUnderCrops(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		//yeah yeah, localCapture, whatever
		Block b = level.getBlockState(pos.above()).getBlock();
		if(b instanceof CrappyDiodeBlock) {
			cir.setReturnValue(true);
		}
	}
}
