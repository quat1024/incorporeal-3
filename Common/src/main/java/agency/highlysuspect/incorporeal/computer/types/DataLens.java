package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.corporea.InvertedCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

/**
 * When a request for data is pulled through a block containing a DataLens, the data may be modified.
 * 
 * Lenses typically "focus" on one piece of the data, blocking out other parts. If an unrelated
 * piece of data comes through the lens, it is typically replaced with Datum.EMPTY ("blocked").
 * But you can technically do whatever you want.
 * 
 * Note to functional programming nerds: It's just a one-way transformation. Not actually a Lens.
 * Sorry for letting you down on this.
 */
public interface DataLens {
	DataType.Datum<?> filter(DataType.Datum<?> input);
	
	//Here, have a couple examples:
	DataLens number = input -> {
		//Numbers pass through the filter unchanged,
		if(input.type() == DataTypes.INTEGER) return input;
		//Solidified requests focus on the integer portion,
		if(input.type() == DataTypes.SOLIDIFIED_REQUEST) return input.<SolidifiedRequest>cast().mapTo(DataTypes.INTEGER, SolidifiedRequest::count);
		//and everything else gets blocked.
		return DataType.Datum.EMPTY;
	};
	
	DataLens matcher = input -> {
		if(input.type() == DataTypes.MATCHER) return input;
		if(input.type() == DataTypes.SOLIDIFIED_REQUEST) return input.<SolidifiedRequest>cast().mapTo(DataTypes.MATCHER, SolidifiedRequest::matcher);
		return DataType.Datum.EMPTY;
	};
	
	DataLens negatory = input -> {
		if(input.type() == DataTypes.INTEGER) return input.<Integer>cast().map(i -> -i);
		if(input.type() == DataTypes.MATCHER) return input.<ICorporeaRequestMatcher>cast().map(InvertedCorporeaRequestMatcher::invert);
		if(input.type() == DataTypes.SOLIDIFIED_REQUEST) {
			//negate both halves
			SolidifiedRequest request = input.<SolidifiedRequest>cast().thing();
			return new DataType.Datum<>(DataTypes.SOLIDIFIED_REQUEST, SolidifiedRequest.create(
				InvertedCorporeaRequestMatcher.invert(request.matcher()),
				-request.count()
			));
		}
		return DataType.Datum.EMPTY;
	};
}
