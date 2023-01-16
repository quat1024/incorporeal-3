package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.InvertedCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.util.SimplerRegistry;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;

/**
 * N.B: the "registry" for these is not (currently?) used, just using it as a convenient place to stick them.
 * They also get referenced (for the ItemLenses) before they actually have a registry name.
 */
public class DataLenses {
	public static final SimplerRegistry<DataLens> REGISTRY = new SimplerRegistry<>();
	
	//The ItemStack.EMPTY of the DataLens world. Does nothing.
	public static final DataLens passthrough = input -> input;
	
	//This one filters the incoming data to integers.
	public static final DataLens number = input -> {
		//Numbers pass through the filter unchanged,
		if(input.type() == DataTypes.INTEGER) return input;
		//Solidified requests focus on the integer portion,
		if(input.type() == DataTypes.SOLIDIFIED_REQUEST) return input.<SolidifiedRequest>cast().mapTo(DataTypes.INTEGER, SolidifiedRequest::count);
		//and everything else gets blocked.
		return Datum.EMPTY;
	};
	
	public static final DataLens matcher = input -> {
		if(input.type() == DataTypes.MATCHER) return input;
		if(input.type() == DataTypes.SOLIDIFIED_REQUEST) return input.<SolidifiedRequest>cast().mapTo(DataTypes.MATCHER, SolidifiedRequest::matcher);
		return Datum.EMPTY;
	};
	
	public static final DataLens negating = input -> {
		if(input.type() == DataTypes.INTEGER) return input.<Integer>cast().map(i -> -i);
		if(input.type() == DataTypes.MATCHER) return input.<CorporeaRequestMatcher>cast().map(InvertedCorporeaRequestMatcher::invert);
		if(input.type() == DataTypes.SOLIDIFIED_REQUEST) {
			//negate both halves
			SolidifiedRequest request = input.<SolidifiedRequest>cast().thing();
			return DataTypes.SOLIDIFIED_REQUEST.datumOf(SolidifiedRequest.create(
				InvertedCorporeaRequestMatcher.invert(request.matcher()),
				-request.count()
			));
		}
		return Datum.EMPTY;
	};
	
	
	public static void registerBuiltinLenses() {
		REGISTRY.register(passthrough, Inc.id("passthrough"));
		REGISTRY.register(number, Inc.id("number"));
		REGISTRY.register(matcher, Inc.id("matcher"));
		REGISTRY.register(negating, Inc.id("negating"));
	}
}
