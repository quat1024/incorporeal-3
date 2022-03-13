package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.computer.types.Datum;

public interface DatumProvider {
	Datum<?> readDatum();
}
