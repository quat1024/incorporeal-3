package agency.highlysuspect.incorporeal.computer.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A value sidecarred with its DataType.
 */
@ParametersAreNonnullByDefault
public record Datum<T>(DataType<T> type, T thing) {
	public static final Datum<Unit> EMPTY = new Datum<>(DataTypes.EMPTY, Unit.INSTANCE);
	
	/// Convenience ///
	
	public int color() {
		return type.color(thing);
	}
	
	public boolean isEmpty() {
		return type == DataTypes.EMPTY;
	}
	
	/**
	 * Returns EMPTY if any datums can't be added together.
	 * Currently this happens every time the datums have different types.
	 * For that reason, this method returns Datum<?> because it may either return Datum<T> or Datum<Unit>.
	 */
	public Datum<?> reduce(Iterable<Datum<?>> others) {
		Datum<T> result = this;
		for(Datum<?> other : others) {
			if(other.type.equals(type)) result = result.map(first -> type.sum(first, other.castAndGet()));
			else return EMPTY;
		}
		return result;
	}
	
	public static Datum<?> reduce(Collection<Datum<?>> datums) {
		if(datums.isEmpty()) return EMPTY;
		
		//lop off the first
		Iterator<Datum<?>> datumerator = datums.iterator();
		//call reduce with it on the rest
		return datumerator.next().reduce(() -> datumerator);
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
	
	//IntelliJ is convinced the Datum<?> -> Datum<X> cast is unneeded
	@SuppressWarnings({"unchecked", "RedundantCast", "RedundantSuppression"})
	public <X> X castAndGet() {
		return ((Datum<X>) this).thing;
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
