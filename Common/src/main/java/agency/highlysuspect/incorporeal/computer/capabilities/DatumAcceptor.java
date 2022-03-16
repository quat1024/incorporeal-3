package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.computer.types.Datum;
import org.jetbrains.annotations.NotNull;

public interface DatumAcceptor extends PositionTweakable {
	void acceptDatum(@NotNull Datum<?> datum);
}
