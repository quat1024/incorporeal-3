package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.AndingCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.util.SimplerRegistry;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * N.B: the "registry" for these is currently used as a fancy list lol
 */
public class DataReducers {
	public static final SimplerRegistry<DataReducer> REGISTRY = new SimplerRegistry<>();
	
	public static final DataReducer INTEGER_SUMMING = DataReducer.reduceSingleType(DataTypes.INTEGER, Integer::sum);
	public static final DataReducer MATCHER_ANDING = DataReducer.reduceSingleType(DataTypes.MATCHER, (a, b) -> AndingCorporeaRequestMatcher.combine(List.of(a, b)));
	public static final DataReducer REQUEST_SUMMING_AND_ANDING = DataReducer.reduceSingleType(DataTypes.SOLIDIFIED_REQUEST, (a, b) ->
		SolidifiedRequest.create(AndingCorporeaRequestMatcher.combine(List.of(a.matcher(), b.matcher())), a.count() + b.count()));
	public static final DataReducer COMBINE_NUMBERS_AND_MATCHER = filteredData -> {
		@Nullable ICorporeaRequestMatcher matcher = null;
		int sum = 0;
		
		for(Datum<?> datum : filteredData) {
			//Numbers are allowed.
			if(datum.type() == DataTypes.INTEGER) {
				sum += datum.<Integer>castAndGet();
				continue;
			}
			
			//Only one matcher is allowed.
			if(datum.type() == DataTypes.MATCHER) {
				if(matcher != null) return Optional.empty(); //More than one matcher
				matcher = datum.castAndGet();
				continue;
			}
			
			//Nothing else is allowed.
			return Optional.empty();
		}
		
		if(matcher == null) return Optional.empty(); //Guess there wasn't a matcher after all?
		else return Optional.of(DataTypes.SOLIDIFIED_REQUEST.datumOf(SolidifiedRequest.create(matcher, sum)));
	};
	
	public static void registerBuiltinReducers() {
		REGISTRY.register(INTEGER_SUMMING, Inc.id("integer_summing"));
		REGISTRY.register(MATCHER_ANDING, Inc.id("matcher_anding"));
		REGISTRY.register(REQUEST_SUMMING_AND_ANDING, Inc.id("request_summing_and_anding"));
		REGISTRY.register(COMBINE_NUMBERS_AND_MATCHER, Inc.id("combine_numbers_and_matcher"));
	}
	
	public static Datum<?> reduce(List<Datum<?>> unfilteredData) {
		List<Datum<?>> filteredData = unfilteredData.stream().filter(d -> !d.isEmpty()).collect(Collectors.toList());
		if(filteredData.isEmpty()) return Datum.EMPTY;
		
		//Try each reducer in turn, until one works
		for(DataReducer reducer : REGISTRY) {
			Optional<Datum<?>> result = reducer.tryReduce(filteredData);
			if(result.isPresent()) return result.get();
		}
		
		return Datum.EMPTY;
	}
}
