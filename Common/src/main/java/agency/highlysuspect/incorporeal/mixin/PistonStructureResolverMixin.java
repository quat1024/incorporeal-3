package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Make pointed datastone stick to itself. This has no effect unless you have a movable block entities mod.
 */
@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {
	@Inject(method = "isSticky", at = @At("HEAD"), cancellable = true)
	private static void isStickyHook(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if(IncBlocks.POINTED_DATASTONE.isHangingDatastone(state) && Inc.INSTANCE.config.pointedDatastoneIsSticky()) {
			cir.setReturnValue(true);
		}
	}
	
	@Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
	private static void canStickToEachOtherHook(BlockState a, BlockState b, CallbackInfoReturnable<Boolean> cir) {
		if(IncBlocks.POINTED_DATASTONE.isHangingDatastone(a)
			&& IncBlocks.POINTED_DATASTONE.isHangingDatastone(b)
			&& Inc.INSTANCE.config.pointedDatastoneIsSticky()) cir.setReturnValue(true);
	}
}
