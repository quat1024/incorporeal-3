package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.computer.types.Datum;
import org.jetbrains.annotations.NotNull;

public interface DatumProvider extends PositionTweakable {
	/**
	 * Not null, but can be Datum.EMPTY, of course.
	 */
	@NotNull Datum<?> readDatum();
}
