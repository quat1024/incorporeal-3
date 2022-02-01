package agency.highlysuspect.incorporeal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.util.Set;

@Mixin(TileCorporeaIndex.class)
public interface TileCorporeaIndexAccessor {
	@Accessor(value = "serverIndexes", remap = false)
	static Set<TileCorporeaIndex> inc$getServerIndices() {
		throw new AssertionError();
	}
}
