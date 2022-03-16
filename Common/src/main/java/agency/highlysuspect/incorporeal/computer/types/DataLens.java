package agency.highlysuspect.incorporeal.computer.types;

import org.jetbrains.annotations.NotNull;

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
	@NotNull Datum<?> filter(@NotNull Datum<?> input);
}
