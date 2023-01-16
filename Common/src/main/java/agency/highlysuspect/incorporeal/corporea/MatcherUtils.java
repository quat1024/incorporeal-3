package agency.highlysuspect.incorporeal.corporea;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.mixin.TileCorporeaRetainerAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utilities for reading and writing ICorporeaRequestMatchers to NBT tags.
 * ICorporeaRequestMatchers are able to serialize themselves to NBT, but the resulting data doesn't include the *type* of
 * matcher that did the serializing, so there's no way to retrieve it.
 */
public class MatcherUtils {
	public static Optional<CorporeaRequestMatcher> tryLoad(CompoundTag nbt) {
		ResourceLocation type = ResourceLocation.tryParse(nbt.getString("type"));
		if(type == null) return Optional.empty();
		
		Map<ResourceLocation, Function<CompoundTag, ? extends CorporeaRequestMatcher>> des = TileCorporeaRetainerAccessor.inc$getDeserializers();
		Function<CompoundTag, ? extends CorporeaRequestMatcher> de = des.get(type);
		
		if(de == null) {
			Inc.LOGGER.warn("Can't deserialize ICorporeaRequestMatcher of type " + type + " as it doesn't have a registered deserializer");
			return Optional.empty();
		}
		
		return Optional.of(de.apply(nbt));
	}
	
	public static CompoundTag save(CorporeaRequestMatcher matcher) {
		Map<Class<? extends CorporeaRequestMatcher>, ResourceLocation> nameMap = TileCorporeaRetainerAccessor.inc$getSerializers();
		ResourceLocation name = nameMap.get(matcher.getClass());
		
		if(name == null) {
			Inc.LOGGER.warn("Can't serialize ICorporeaRequestMatcher of class " + matcher.getClass().getSimpleName() + " as it doesn't have a registered ID");
			return new CompoundTag();
		} else {
			CompoundTag tag = new CompoundTag();
			tag.putString("type", name.toString());
			matcher.writeToNBT(tag);
			return tag;
		}
	}
}
