package agency.highlysuspect.incorporeal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Mixin(TileCorporeaIndex.class)
public interface TileCorporeaIndexAccessor {
	/**
	 * This is a set of all corporea indices on the entire Minecraft server. Corporea index code uses this
	 * to determine if you're close to a corporea index when you send a chat message.
	 * 
	 * I need to write something similar to that code for the Sanvocalia, which needs to detect nearby
	 * corporea indices, and figured doing it the same way that Botania does would be a good idea.
	 * 
	 * @see agency.highlysuspect.incorporeal.corporea.IndexFinder
	 */
	@Accessor(value = "serverIndexes", remap = false)
	static Set<TileCorporeaIndex> inc$getServerIndices() {
		throw new AssertionError("mixin failed to apply");
	}
	
	/**
	 * And this is the list of regular expressions, used to match chat messages to corporea requests.
	 * 
	 * @see agency.highlysuspect.incorporeal.item.TicketConjurerItem
	 */
	@Accessor(value = "patterns", remap = false)
	static Map<Pattern, TileCorporeaIndex.IRegexStacker> inc$getPatterns() {
		throw new AssertionError("mixin failed to apply");
	}
}
