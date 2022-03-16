package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.computer.types.Datum;
import org.jetbrains.annotations.NotNull;

/**
 * Something that can accept (or "sink") a datum.
 */
public interface DatumAcceptor extends PositionTweakable {
	void acceptDatum(@NotNull Datum<?> datum);
}
