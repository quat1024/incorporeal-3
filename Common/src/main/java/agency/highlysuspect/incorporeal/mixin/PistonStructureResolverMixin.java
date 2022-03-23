package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.IncBlocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {
	@Inject(method = "isSticky", at = @At("HEAD"), cancellable = true)
	private static void isStickyHook(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if(state.getBlock() == IncBlocks.POINTED_DATASTONE) cir.setReturnValue(true);
	}
	
	@Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
	private static void canStickToEachOtherHook(BlockState a, BlockState b, CallbackInfoReturnable<Boolean> cir) {
		if(a.getBlock() == IncBlocks.POINTED_DATASTONE && b.getBlock() == IncBlocks.POINTED_DATASTONE) cir.setReturnValue(true);
	}
}
