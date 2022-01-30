package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(TileCorporeaRetainer.class)
public abstract class TileCorporeaRetainerMixin implements RetainerDuck {
	@Shadow(remap = false) @Nullable private ICorporeaRequestMatcher request;
	@Shadow(remap = false) private int requestCount;
	@Shadow(remap = false) private int compValue;
	
	@Override
	@Nullable
	public ICorporeaRequestMatcher inc$getMatcher() {
		return request;
	}
	
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
	
	@Override
	public void inc$setCount(int newCount) {
		requestCount = newCount;
		
		int oldComparatorValue = compValue;
		int newComparatorValue = CorporeaHelper.instance().signalStrengthForRequestSize(newCount);
		
		if(oldComparatorValue != newComparatorValue) {
			compValue = newComparatorValue;
			
			if(compValue == 0) {
				request = null; //clears the request
			}
			
			((BlockEntity) (Object) this).setChanged();
		}
	}
}
