package agency.highlysuspect.incorporeal.computer.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
	
	/**
	 * Sums the inputs; used in the Data Prism.
	 */
	@SuppressWarnings("unchecked") //Can't put SafeVarargs as an interface contract. Play nice, please.
	T sum(T... inputs);
	
	/**
	 * A value sidecarred with its DataType.
	 */
	@ParametersAreNonnullByDefault
	record Datum<T>(DataType<T> type, T thing) {
		public static final Datum<Unit> EMPTY = new Datum<>(DataTypes.EMPTY, Unit.INSTANCE);
		
		/// Weird bullshit ///
		
		/**
		 * Applies a non-type-changing transformation to the piece of data.
		 */
		public Datum<T> map(UnaryOperator<T> mapper) {
			return new Datum<>(type, mapper.apply(thing));
		}
		
		/**
		 * Applies a type-changing transformation to the piece of data.
		 */
		public <X> Datum<X> mapTo(DataType<X> newType, Function<T, X> mapper) {
			return new Datum<>(newType, mapper.apply(thing));
		}
		
		/**
		 * This is typically used to cast from Datum<?> to a specific type,
		 * after you've already checked that the cast is safe, of course.
		 */
		@SuppressWarnings("unchecked")
		public <X> Datum<X> cast() {
			return (Datum<X>) this;
		}
		
		/// NBT ///
		
		/**
		 * Save this Datum, including information about which type it is, to NBT.
		 */
		public CompoundTag save() {
			ResourceLocation id = DataTypes.REGISTRY.getKey(type);
			
			CompoundTag tag = new CompoundTag();
			type.save(thing, tag);
			if(tag.contains("type")) {
				//Catch corruptions the "type" field that I'm using to disambiguate between different DataTypes.
				//Because I know i'm gonna do this on accident.
				throw new IllegalStateException("Don't add a key named 'type' to DataType<" + thing.getClass().getSimpleName() + ">, please");
			}
			
			tag.putString("type", id.toString());
			return tag;
		}
		
		/**
		 * Load this Datum from an NBT tag that includes type information.
		 */
		//MMmmmmmmmm yummm yummmmmm i lvoe Java Generic's
		@SuppressWarnings("unchecked")
		public static <T> Datum<T> load(CompoundTag tag) {
			ResourceLocation id = ResourceLocation.tryParse(tag.getString("type"));
			
			DataType<T> type = (DataType<T>) DataTypes.REGISTRY.get(id);
			if(type == null) return EMPTY.cast();
			
			Optional<T> thing = type.tryLoad(tag);
			if(thing.isEmpty()) return EMPTY.cast();
			
			return new Datum<>(type, thing.get());
		}
		
		/// Forwarding equals and hashcode through to virtual functions on DataType ///
		///                   aka defeating the purpose of a record                 ///
		
		//IntelliJ is convinced the Datum<?> -> Datum<T> cast is unneeded, but it totally is lol?
		@SuppressWarnings({"unchecked", "RedundantCast", "RedundantSuppression"})
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			
			Datum<?> other = (Datum<?>) o;
			
			if(!type.equals(other.type)) return false;
			return type.equals(thing, ((Datum<T>) other).thing);
		}
		
		@Override
		public int hashCode() {
			int result = type.hashCode();
			result = 31 * result + type.hashCode(thing);
			return result;
		}
	}
}
