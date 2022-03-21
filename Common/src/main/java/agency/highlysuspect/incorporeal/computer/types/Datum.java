package agency.highlysuspect.incorporeal.computer.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A value, sidecarred with its DataType.
 * The DataType augments the value with matters relating to the computermod world: how to save and load
 * from NBT, what color it is, what comparator signal it emits, and so on.
 */
@ParametersAreNonnullByDefault
public record Datum<T>(DataType<T> type, T thing) {
	/**
	 * The empty datum, corresponding to no value.
	 */
	public static final Datum<Unit> EMPTY = new Datum<>(DataTypes.EMPTY, Unit.INSTANCE);
	
	/// Convenience ///
	
	public int color() {
		return type.color(thing);
	}
	
	public boolean isEmpty() {
		return type == DataTypes.EMPTY;
	}
	
	public int signal() {
		return type.signal(thing);
	}
	
	public Component describe() {
		return type.describe(thing);
	}
	
	/// Weird bullshit ///
	
	/**
	 * Applies a non-type-changing transformation to the piece of data.
	 */
	public Datum<T> map(UnaryOperator<T> mapper) {
		return new Datum<>(type, mapper.apply(thing));
	}
	
	/**
	 * Applies a type-changing transformation to the piece of data.
	 * Known as "hoist", in the literature, I've heard?
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
	
	/**
	 * Shortcut for datum.<Type>cast().thing().
	 * Having it combined into one method sometimes helps javac's type inference
	 * and you can skip the .cast() call, which usually needs an annotation.
	 */
	//IntelliJ is convinced the Datum<?> -> Datum<X> cast is unneeded
	@SuppressWarnings({"unchecked", "RedundantCast", "RedundantSuppression"})
	public <X> X castAndGet() {
		return ((Datum<X>) this).thing;
	}
	
	/// NBT ///
	
	/**
	 * Save this Datum, including information about which type it is, to an NBT tag.
	 */
	public CompoundTag save() {
		ResourceLocation id = DataTypes.REGISTRY.getKey(type);
		
		CompoundTag tag = new CompoundTag();
		type.save(thing, tag);
		if(tag.contains("type")) {
			//Catch corruptions of the "type" field that I'm using to disambiguate between different DataTypes.
			//Because I know i'm gonna do this on accident.
			//UPDATE: I have done this on accident.
			throw new IllegalStateException("Don't add a key named 'type' to DataType<" + thing.getClass().getSimpleName() + ">, please");
		}
		
		tag.putString("type", id.toString());
		return tag;
	}
	
	/**
	 * Load a Datum from an NBT tag, that includes information about what type it is.
	 */
	//MMmmmmmmmm yummm yummmmmm i lvoe Java Generic's
	public static Datum<?> load(CompoundTag tag) {
		return load0(tag);
	}
	
	//I just need to be able to name the generic... (java moment)
	@SuppressWarnings("unchecked")
	private static <T> Datum<T> load0(CompoundTag tag) {
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
