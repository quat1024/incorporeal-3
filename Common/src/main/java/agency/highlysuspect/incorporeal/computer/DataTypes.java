package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DataTypes {
	public static final Map<Class<?>, DataType<?>> typesByClass = new HashMap<>();
	public static final Map<ResourceLocation, DataType<?>> typesById = new HashMap<>();
	
	public static CompoundTag save(Object thing) {
		DataType<?> serializer = typesByClass.get(thing.getClass());
		if(serializer == null) throw new IllegalArgumentException("No serializer for class " + thing.getClass());
		
		CompoundTag tag = new CompoundTag();
		tag.putString("type", serializer.id().toString());
		serializer.erasedSave(thing, tag);
		return tag;
	}
	
	public static @Nullable Object load(CompoundTag tag) {
		ResourceLocation type = ResourceLocation.tryParse(tag.getString("type"));
		if(type == null) return null; //assume the nbt tag was empty or something.. need a much nicer story surrounding this tbh
		
		DataType<?> serializer = typesById.get(type);
		
		//todo, probably the wrong handling wrt. removing an addon mod that adds a serializer
		if(serializer == null) throw new IllegalStateException("No serializer with id " + type);
		
		else return serializer.load(tag);
	}
	
	//This is not public API at the moment.
	//Let me know if you want it to be
	private static void register(DataType<?> type) {
		typesByClass.put(type.typeClass(), type);
		typesById.put(type.id(), type);
	}
	
	public static void registerBuiltinTypes() {
		register(new IntegerDataType());
		register(new CorporeaRequestMatcherDataType());
		
		register(new SolidifiedRequestComplexType());
	}
	
	//TODO: This really needs to be a capability or something.
	// Also needs to work on more than BlockEntities
	public static boolean canExtractData(BlockEntity be) {
		return be instanceof RetainerDuck || be instanceof DataStorageBlockEntity;
	}
	
	//and this
	public static @Nullable Object extractData(BlockEntity be) {
		if(be instanceof DataStorageBlockEntity storage) {
			return storage.getData();
		}
		
		if(be instanceof RetainerDuck retainer) { //TileCorporeaRetainer
			return retainer.inc$asSolidifiedRequest();
		}
		
		return null;
	}
	
	//This too
	public static boolean canInjectData(BlockEntity be) {
		return be instanceof RetainerDuck || be instanceof DataStorageBlockEntity;
	}
	
	//this as well
	public static void injectData(BlockEntity be, @Nullable Object data) {
		if(be instanceof RetainerDuck retainer) {
			if(data instanceof SolidifiedRequest sr) retainer.inc$liquifactRequest(sr);
			else if(data == null) retainer.inc$liquifactRequest(SolidifiedRequest.EMPTY);
		}
		
		if(be instanceof DataStorageBlockEntity storage) {
			storage.setData(data);
		}
	}
	
	public static @Nullable Object filterData(Level level, BlockPos pos, @Nullable Object data) {
		if(data != null && level.getBlockState(pos).getBlock() instanceof LensBlock lensBlock) return lensBlock.lens.filter(data);
		else return data;
	}
}
