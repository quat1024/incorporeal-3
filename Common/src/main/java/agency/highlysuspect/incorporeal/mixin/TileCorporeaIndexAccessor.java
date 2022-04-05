package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.util.Map;
import java.util.regex.Pattern;

@Mixin(TileCorporeaIndex.class)
public interface TileCorporeaIndexAccessor {
	/**
	 * This is a list of regular expression thingies used to match chat messages to corporea requests.
	 * 
	 * @see TicketConjurerItem
	 */
	@Accessor(value = "patterns", remap = false)
	static Map<Pattern, TileCorporeaIndex.IRegexStacker> inc$getPatterns() {
		throw new AssertionError("mixin failed to apply");
	}
}
