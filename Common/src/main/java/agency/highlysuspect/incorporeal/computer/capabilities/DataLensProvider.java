package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.computer.types.DataLens;
import org.jetbrains.annotations.NotNull;

/**
 * Something that provides a DataLens.
 * If you want nullability, return DataLenses.passthrough. It's a lens that does nothing.
 */
public interface DataLensProvider extends PositionTweakable {
	@NotNull DataLens getLens();
}
