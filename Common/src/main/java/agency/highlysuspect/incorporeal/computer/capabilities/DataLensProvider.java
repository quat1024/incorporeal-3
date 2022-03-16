package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.computer.types.DataLens;
import org.jetbrains.annotations.NotNull;

public interface DataLensProvider extends PositionTweakable {
	@NotNull DataLens getLens();
}
