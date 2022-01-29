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

@Mixin(TileCorporeaInterceptor.class)
public class TileCorporeaInterceptorMixin {
	@Inject(
		method = "interceptRequestLast",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private void whenIntercepting(ICorporeaRequestMatcher request, int count, ICorporeaSpark spark, ICorporeaSpark source, List<ItemStack> stacks, List<ICorporeaNode> nodes, boolean doit, CallbackInfo ci) {
		Level level = ((TileCorporeaInterceptor) (Object) this).getLevel();
		assert level != null;
		BlockPos pos = ((TileCorporeaInterceptor) (Object) this).getBlockPos();
		
		//This is a somewhat sloppy mixin, Botania already does this exact iteration. But I figured instead of trying to target the
		//inside of a loop, which means a nasty local-capture injection, it's not so bad if I just loop again.
		
		//The reason this uses a mixin in the first place is because TileCorporeaInterceptor checks instanceof TileCorporeaRetainer,
		//and ever since they added BlockEntityTypes it's not possible to extend blockentities, so I needed to do something different
		//with the Corporea Solidifier. At least it doesn't actually have to be a BlockEntity, like I did in 1.12.
		
		for(Direction dir : Direction.values()) {
			BlockPos solidifierPos = pos.relative(dir);
			BlockState state = level.getBlockState(solidifierPos);
			if(state.getBlock() instanceof CorporeaSolidifierBlock solidifier) {
				solidifier.receiveRequest(level, solidifierPos, SolidifiedRequest.create(request, count));
			}
		}
	}
}
