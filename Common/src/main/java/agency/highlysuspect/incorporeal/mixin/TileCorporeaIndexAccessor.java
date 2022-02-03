package agency.highlysuspect.incorporeal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.util.Set;

@Mixin(TileCorporeaIndex.class)
public interface TileCorporeaIndexAccessor {
	/**
	 * This is a set of all corporea indices on the entire Minecraft server. Corporea index code uses this
	 * to determine if you're close to a corporea index when you send a chat message.
	 * 
	 * I need to write something similar to that code for the Sanvocalia, which needs to detect nearby
	 * corporea indices, and figured doing it the same way that Botania does would be a good idea.
	 * 
	 * Now that I think about it, it wouldn't be that hard to manually iterate nearby BlockEntities
	 * and see if they are item frames, instead. So this mixin could probably be removed.
	 */
	@Accessor(value = "serverIndexes", remap = false)
	static Set<TileCorporeaIndex> inc$getServerIndices() {
		throw new AssertionError();
	}
}
