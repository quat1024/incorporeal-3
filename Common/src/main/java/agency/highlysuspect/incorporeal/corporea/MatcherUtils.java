package agency.highlysuspect.incorporeal.corporea;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.mixin.TileCorporeaRetainerAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utilities for reading and writing ICorporeaRequestMatchers to NBT tags.
 * Probably should be part of Botania's real API, now that I think about it.
 */
public class MatcherUtils {
	public static Optional<ICorporeaRequestMatcher> tryFromTag(CompoundTag nbt) {
		ResourceLocation type = ResourceLocation.tryParse(nbt.getString("type"));
		if(type == null) return Optional.empty();
		
		Map<ResourceLocation, Function<CompoundTag, ? extends ICorporeaRequestMatcher>> des = TileCorporeaRetainerAccessor.inc$getDeserializers();
		Function<CompoundTag, ? extends ICorporeaRequestMatcher> de = des.get(type);
		
		if(de == null) {
			Inc.LOGGER.warn("Can't deserialize ICorporeaRequestMatcher of type " + type + " as it doesn't have a registered deserializer");
			return Optional.empty();
		}
		
		return Optional.of(de.apply(nbt));
	}
	
	public static CompoundTag toTag(ICorporeaRequestMatcher matcher) {
		Map<Class<? extends ICorporeaRequestMatcher>, ResourceLocation> nameMap = TileCorporeaRetainerAccessor.inc$getSerializers();
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
