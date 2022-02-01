package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.integration.corporea.CorporeaNodeDetectors;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * Main entrypoint for the mod, or, as close as you can get in the multiloader world.
 */
public class Inc {
	public static final String MODID = "incorporeal";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static void registerExtraThings() {
		CorporeaHelper.instance().registerRequestMatcher(id("empty"), EmptyCorporeaRequestMatcher.class, __ -> EmptyCorporeaRequestMatcher.INSTANCE);
		CorporeaNodeDetectors.register(new RedStringLiarBlockEntity.NodeDetector());
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static <T> T choose(List<T> list, Random random) {
		return list.get(random.nextInt(list.size()));
	}
	
	public static float rangeRemap(float value, float low1, float high1, float low2, float high2) {
		//The value goes from low1..high1, remap that range to 0..1
		float x = (value - low1) / (high1 - low1);
		//The value goes from 0..1, remap that range to low2..high2
		return x * (high2 - low2) + low2;
	}
	
	public static float sinDegrees(float in) {
		return Mth.sin((in % 360) * (float) (Math.PI / 180));
	}
	
	public static float cosDegrees(float in) {
		return Mth.cos((in % 360) * (float) (Math.PI / 180));
	}
	
	public static <T> Map<DyeColor, T> sixteenColors(Function<DyeColor, T> maker) {
		Map<DyeColor, T> map = new EnumMap<>(DyeColor.class);
		for(DyeColor color : DyeColor.values()) map.put(color, maker.apply(color));
		return map;
	}
}
