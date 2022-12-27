package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Augments the Corporea Retainer with getters/setters for reading and writing its ICorporeaRequestMatcher
 * and its request count. Tries to setChanged and update comparator signal at appropriate times.
 */
@Mixin(TileCorporeaRetainer.class)
public abstract class TileCorporeaRetainerMixin implements RetainerDuck {
	@Shadow(remap = false) @Nullable private ICorporeaRequestMatcher request;
	@Shadow(remap = false) private int requestCount;

	@Shadow public abstract int getComparatorValue();

	@Override
	@Nullable
	public ICorporeaRequestMatcher inc$getMatcher() {
		return request;
	}
	
	@SuppressWarnings("ConstantConditions") //doublecast
	@Override
	public void inc$setMatcher(@Nullable ICorporeaRequestMatcher matcher) {
		if(!Objects.equals(request, matcher)) {
			request = matcher;
			((BlockEntity) (Object) this).setChanged();
		}
	}
	
	@Override
	public int inc$getCount() {
		return requestCount;
	}
	
	@SuppressWarnings("ConstantConditions") //doublecast
	@Override
	public void inc$setCount(int newCount) {
		int oldComparatorValue = getComparatorValue();
		requestCount = newCount;
		int newComparatorValue = getComparatorValue();

		if(oldComparatorValue != newComparatorValue) {
			if(newComparatorValue == 0) {
				request = null; //clears the request
			}
			
			((BlockEntity) (Object) this).setChanged();
		}
	}
}
