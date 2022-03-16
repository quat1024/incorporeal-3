package agency.highlysuspect.incorporeal.computer.types;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public interface DataReducer {
	/**
	 * Potentially reduce this soup of data (that might contain data of different types) into a single datum.
	 * The data is "filtered", in that none of it has the type DataTypes.EMPTY.
	 */
	Optional<Datum<?>> tryReduce(List<Datum<?>> filteredData);
	
	/**
	 * Helper that performs a single-type reduction operation. If the entire dataset is of the same type, the
	 * reducer function is applied to successive pairs of the data set, and a datum containing the final reduced result
	 * is returned.
	 */
	static <T> Optional<Datum<?>> reduceSingleType(DataType<T> type, List<Datum<?>> data, BiFunction<T, T, T> reducer) {
		//check that all the types match first, before doing any computations
		if(data.isEmpty()) return Optional.empty();
		for(Datum<?> datum : data) {
			if(datum.type() != type) return Optional.empty();
		}
		
		//now perform the reduction
		T result = null;
		for(Datum<?> datum : data) {
			if(result == null) result = datum.castAndGet(); //data.isEmpty() was checked, so this branch will always be taken once
			else result = reducer.apply(result, datum.castAndGet());
		}
		
		return Optional.of(type.datumOf(result));
	}
	
	static <T> DataReducer reduceSingleType(DataType<T> type, BiFunction<T, T, T> reducer) {
		return filteredData -> reduceSingleType(type, filteredData, reducer);
	}
}
