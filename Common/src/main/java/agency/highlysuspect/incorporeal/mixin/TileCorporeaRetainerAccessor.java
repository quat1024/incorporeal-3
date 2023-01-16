package agency.highlysuspect.incorporeal.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.common.block.block_entity.corporea.CorporeaRetainerBlockEntity;

import java.util.Map;
import java.util.function.Function;

/**
 * Implementation detail of Incorporeal's MatcherUtils class, which helps de/serialize ICorporeaRequestMatchers
 * as blocks of NBT data.
 * 
 * In Botania, the only thing that can store corporea requests as NBT is the Corporea Retainer, so
 * anything you throw at CorporeaHelper.registerRequestMatcher ultimately ends up in these static
 * Corporea Retainer fields.
 * 
 * Retrieving the serializers again should probably be a part of CorporeaHelper's public API, though. :)
 * 
 * @see agency.highlysuspect.incorporeal.corporea.MatcherUtils
 */
@Mixin(CorporeaRetainerBlockEntity.class)
public interface TileCorporeaRetainerAccessor {
	@Accessor(value = "corporeaMatcherDeserializers", remap = false)
	static Map<ResourceLocation, Function<CompoundTag, ? extends CorporeaRequestMatcher>> inc$getDeserializers() {
		throw new AssertionError();
	}
	
	@Accessor(value = "corporeaMatcherSerializers", remap = false)
	static Map<Class<? extends CorporeaRequestMatcher>, ResourceLocation> inc$getSerializers() {
		throw new AssertionError();
	}
}
