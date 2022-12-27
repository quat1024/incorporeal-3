package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.corporea.ICorporeaNode;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.block.tile.corporea.TileCorporeaInterceptor;

import java.util.List;
import java.util.Set;

@Mixin(TileCorporeaInterceptor.class)
public class TileCorporeaInterceptorMixin {
	/**
	 * This method is called when the Corporea Interceptor is about to intercept a request. I need to be aware
	 * of this, so I can tell nearby Corporea Solidifiers about this request.
	 * 
	 * This is a somewhat sloppy mixin, Botania already does this exact iteration when finding corporea retainers.
	 * But I figured instead of trying to target the inside of a loop, which is a nasty local-capture injection,
	 * it's not so bad if I just go around the loop again.
	 * 
	 * TileCorporeaInterceptor simply checks `instanceof TileCorporeaRetainer` to do its insertion, but the solidifier
	 * is not a corporea retainer, so here we are. 
	 */
	@Inject(
		method = "interceptRequestLast",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	@SuppressWarnings("ConstantConditions") //Doublecasts
	private void whenIntercepting(ICorporeaRequestMatcher request, int count, ICorporeaSpark spark, ICorporeaSpark source, List<ItemStack> stacks, Set<ICorporeaNode> nodes, boolean doit, CallbackInfo ci) {
		Level level = ((TileCorporeaInterceptor) (Object) this).getLevel();
		assert level != null;
		BlockPos pos = ((TileCorporeaInterceptor) (Object) this).getBlockPos();
		
		for(Direction dir : Direction.values()) {
			BlockPos solidifierPos = pos.relative(dir);
			BlockState state = level.getBlockState(solidifierPos);
			if(state.getBlock() instanceof CorporeaSolidifierBlock solidifier) {
				solidifier.receiveRequest(level, solidifierPos, SolidifiedRequest.create(request, count));
			}
		}
	}
}
