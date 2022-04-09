package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.computer.types.Datum;
import org.jetbrains.annotations.NotNull;

/**
 * Something that can provide (or "source") a datum.
 * Not null, but may be Datum.EMPTY.
 */
public interface DatumProvider extends PositionTweakable {
	@NotNull default Datum<?> readDatum() {
		return readDatum(true);
	}
	
	@NotNull Datum<?> readDatum(boolean doIt);
}
