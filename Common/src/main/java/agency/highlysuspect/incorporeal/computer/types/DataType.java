package agency.highlysuspect.incorporeal.computer.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

/**
 * All the information needed for a type T to participate in the computermod world.
 * 
 * Important: You MUST override at least one of "infallibleLoad" or "tryLoad".
 */
@ParametersAreNonnullByDefault
public interface DataType<T> {
	/**
	 * Persist this thing to this NBT tag.
	 * Do not write to the key "type"; it's used to disambiguate between different DataTypes when loading.
	 */
	void save(T thing, CompoundTag tag);
	
	/**
	 * Unconditionally loads a thing from an NBT tag.
	 * If the thing cannot be loaded, throws an exception.
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent") //Yes, that's the point, it throws an exception.
	default T infallibleLoad(CompoundTag tag) {
		return tryLoad(tag).get();
	}
	
	/**
	 * Attempts to load this thing from an NBT tag.
	 * If the thing cannot be loaded, returns Optional.empty(). 
	 */
	default Optional<T> tryLoad(CompoundTag tag) {
		return Optional.of(infallibleLoad(tag));
	}
	
	/**
	 * Returns an integer globally unique per DataType that's passed into an item property override on the conjurer and ticket items....
	 * Yea im not very good at item models
	 */
	int magicNumber();
	
	/**
	 * Returns a color corresponding to this thing.
	 * A value may not be provided, wherein a color corresponding to the data type should be returned instead.
	 */
	int color(@Nullable T thing);
	
	default int color() {
		return color(null);
	}
	
	/**
	 * Returns what comparator signal strength this thing should emit.
	 */
	int signal(T thing);
	
	/**
	 * Summarize this thing, for display on a Ticket item.
	 * For Tickets, it will be formatted into the item's display name.
	 * No need to include text like "Corporea Ticket".
	 */
	Component describe(T thing);
	
	/**
	 * Parse this thing out of a chat message, for use with the Ticket Conjurer.
	 * If the player is holding a Ticket Conjurer in their right hand, "otherHand" contains what they're holding in their left hand.
	 * And vice versa.
	 */
	T parse(String message, ItemStack otherHand);
	
	//convenience
	default Datum<T> parseToDatum(String message, ItemStack otherHand) {
		return datumOf(parse(message, otherHand));
	}
	
	/**
	 * Returns whether these two things are equal.
	 * By default, forwards to Objects.equals, which works on most types.
	 */
	default boolean equals(T a, T b) {
		return Objects.equals(a, b);
	}
	
	/**
	 * Computes the hashCode of this thing.
	 * By default, forwards to thing#hashCode, which works on most types.
	 */
	default int hashCode(T thing) {
		return thing.hashCode();
	}
	
	//Convenience
	default Datum<T> datumOf(T thing) {
		return new Datum<>(this, thing);
	}
}
