package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.corporea.CorporeaHelper;

/**
 * Main entrypoint for the mod, or, as close as you can get in the multiloader world.
 */
public class Inc {
	public static final String MODID = "incorporeal";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static void init() {
		CorporeaHelper.instance().registerRequestMatcher(id("empty"), EmptyCorporeaRequestMatcher.class, __ -> EmptyCorporeaRequestMatcher.INSTANCE);
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
}
