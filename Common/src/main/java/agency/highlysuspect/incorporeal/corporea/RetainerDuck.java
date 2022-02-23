package agency.highlysuspect.incorporeal.corporea;

import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import javax.annotation.Nullable;

/**
 * The corporea retainer implements this interface.
 * @see agency.highlysuspect.incorporeal.mixin.TileCorporeaRetainerMixin
 * Its main purpose is to provide a nice getter/setter API, that takes care of marking dirty, setting the comparator level, etc.
 */
public interface RetainerDuck {
	@Nullable ICorporeaRequestMatcher inc$getMatcher();
	void inc$setMatcher(@Nullable ICorporeaRequestMatcher matcher);
	
	int inc$getCount();
	void inc$setCount(int count);
	
	//Inc3 deals with SolidifiedRequest a lot less, but there might still be handy
	default SolidifiedRequest inc$asSolidifiedRequest() {
		return SolidifiedRequest.create(inc$getMatcher(), inc$getCount());
	}
	
	default void inc$liquifactRequest(SolidifiedRequest request) {
		inc$setMatcher(request.matcher());
		inc$setCount(request.count());
	}
}
