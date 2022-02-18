package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.corporea.InvertedCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

public interface DataLens {
	@Nullable Object filter(Object input);
	
	//focuses on the number - numbers pass through, solidifiedrequests get split into their number
	DataLens number = input -> {
		if(input instanceof Integer) return input;
		else if(input instanceof SolidifiedRequest sr) return sr.count();
		else return null;
	};
	
	//focuses on the matcher - matchers pass through, solidifiedrequests get split into their matcher
	DataLens matcher = input -> {
		if(input instanceof ICorporeaRequestMatcher) return input;
		else if(input instanceof SolidifiedRequest sr) return sr.matcher();
		else return null;
	};
	
	//negates things that pass through it
	DataLens negatory = input -> {
		if(input instanceof Integer i) return -i;
		else if(input instanceof ICorporeaRequestMatcher matcher) return InvertedCorporeaRequestMatcher.invert(matcher);
		//commented out for gameplay reasons (negate both halves yourself!)
//		else if(input instanceof SolidifiedRequest sr) return SolidifiedRequest.create(
//			InvertedCorporeaRequestMatcher.invert(sr.matcher()),
//			-sr.count()
//		);
		else return null;
	};
}
