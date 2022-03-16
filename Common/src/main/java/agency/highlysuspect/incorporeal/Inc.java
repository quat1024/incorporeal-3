package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.computer.types.DataLenses;
import agency.highlysuspect.incorporeal.computer.types.DataReducers;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import agency.highlysuspect.incorporeal.corporea.AndingCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.InvertedCorporeaRequestMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.integration.corporea.CorporeaNodeDetectors;
import vazkii.botania.common.lib.LibMisc;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * Main entrypoint for the mod, or, as close as you can get in the multiloader world I guess.
 * 
 * Also contains random bits of utility crap. Noone will spot your haphazard "util" package if you
 * throw them all in here. How's that.
 */
public class Inc {
	public static final String MODID = "incorporeal";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	//Called after Botania's initializer on Fabric and in CommonInit on forge
	public static void registerExtraThings() {
		//corporea matchers
		CorporeaHelper.instance().registerRequestMatcher(id("empty"), EmptyCorporeaRequestMatcher.class, __ -> EmptyCorporeaRequestMatcher.INSTANCE);
		CorporeaHelper.instance().registerRequestMatcher(id("not"), InvertedCorporeaRequestMatcher.class, InvertedCorporeaRequestMatcher::readFromNBT);
		CorporeaHelper.instance().registerRequestMatcher(id("and"), AndingCorporeaRequestMatcher.class, AndingCorporeaRequestMatcher::readFromNbt);
		
		//corporea node detectors
		CorporeaNodeDetectors.register(new RedStringLiarBlockEntity.NodeDetector());
		CorporeaNodeDetectors.register(new EnderSoulCoreBlockEntity.NodeDetector());
		
		//computer stuff
		DataTypes.registerBuiltinTypes();
		DataReducers.registerBuiltinReducers();
		DataLenses.registerBuiltinLenses();
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static ResourceLocation botaniaId(String path) {
		return new ResourceLocation(LibMisc.MOD_ID, path);
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
