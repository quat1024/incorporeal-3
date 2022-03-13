package agency.highlysuspect.incorporeal.corporea;

import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nullable;

/**
 * The corporea retainer implements this interface.
 * Its main purpose is to provide a nice getter/setter API, that takes care of marking dirty, setting the comparator level, etc.
 * @see TileCorporeaRetainer
 * @see agency.highlysuspect.incorporeal.mixin.TileCorporeaRetainerMixin
 */
public interface RetainerDuck {
	@Nullable ICorporeaRequestMatcher inc$getMatcher();
	void inc$setMatcher(@Nullable ICorporeaRequestMatcher matcher);
	
	int inc$getCount();
	void inc$setCount(int count);
	
	//Inc3 deals with SolidifiedRequest a lot less, but these might still be handy
	default SolidifiedRequest inc$asSolidifiedRequest() {
		return SolidifiedRequest.create(inc$getMatcher(), inc$getCount());
	}
	
	default void inc$liquifactRequest(SolidifiedRequest request) {
		inc$setMatcher(request.matcher());
		inc$setCount(request.count());
	}
	
	//Convenience ;) sometimes i only have a RetainerDuck and no TileCorporeaRetainer due to pattern variables
	default boolean inc$hasPendingRequest() {
		return ((TileCorporeaRetainer) this).hasPendingRequest();
	}
}
