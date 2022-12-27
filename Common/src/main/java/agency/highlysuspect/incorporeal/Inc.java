package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.computer.types.DataLenses;
import agency.highlysuspect.incorporeal.computer.types.DataReducers;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import agency.highlysuspect.incorporeal.corporea.AndingCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.InvertedCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.platform.IncBootstrapper;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.DispenserBlock;
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
 * Secondary entrypoint for the mod.
 * The primary entrypoints are modloader specific. Each primary entrypoint calls "Inc.onInitialize()".
 */
public class Inc {
	public static final String MODID = "incorporeal";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static Inc INSTANCE = new Inc(IncXplat.INSTANCE.createBootstrapper());
	
	private Inc(IncBootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
		this.config = new IncConfig(bootstrapper.createConfigBuilder(MODID));
	}
	
	public final IncConfig config;
	
	private final IncBootstrapper bootstrapper;
	private boolean selfInit = false;
	private boolean botaniaInit = false;
	private boolean bothInit = false;
	
	public void onInitialize() {
		//platform-specific init
		bootstrapper.registerBlocks();
		bootstrapper.registerBlockEntityTypes();
		bootstrapper.registerItems();
		bootstrapper.registerEntityTypes();
		bootstrapper.registerEntityAttributes();
		bootstrapper.registerSoundEvents();
		bootstrapper.registerServerToClientNetworkChannelSender();
		bootstrapper.registerCapabilities();
		bootstrapper.registerCommands();
		
		//common init
		DataTypes.registerBuiltinTypes();
		DataReducers.registerBuiltinReducers();
		DataLenses.registerBuiltinLenses();
		
		//post
		selfInit = true;
		bootstrapper.endSelfInit();
		tryAfterBotaniaInitialization();
	}
	
	//Call this after you're sure Botania is done initializing.
	//It's okay to do this in like, "post init" / "common setup" as well, just do it before a world loads.
	public void markBotaniaAsDoneInitializing() {
		botaniaInit = true;
		tryAfterBotaniaInitialization();
	}
	
	private void tryAfterBotaniaInitialization() {
		if(!bothInit && selfInit && botaniaInit) {
			bothInit = true;
			
			//platform specific post-botania init
			bootstrapper.registerCorporeaIndexCallback();
			bootstrapper.registerRedstoneRootPlaceEvent();
			
			//common post-botania init
			//corporea matchers
			CorporeaHelper.instance().registerRequestMatcher(id("empty"), EmptyCorporeaRequestMatcher.class, __ -> EmptyCorporeaRequestMatcher.INSTANCE);
			CorporeaHelper.instance().registerRequestMatcher(id("not"), InvertedCorporeaRequestMatcher.class, InvertedCorporeaRequestMatcher::readFromNBT);
			CorporeaHelper.instance().registerRequestMatcher(id("not-fallback"), InvertedCorporeaRequestMatcher.Fallback.class, __ -> InvertedCorporeaRequestMatcher.Fallback.INSTANCE);
			CorporeaHelper.instance().registerRequestMatcher(id("and"), AndingCorporeaRequestMatcher.class, AndingCorporeaRequestMatcher::readFromNbt);
			
			//corporea node detectors
			CorporeaNodeDetectors.register(new RedStringLiarBlockEntity.NodeDetector());
			CorporeaNodeDetectors.register(new EnderSoulCoreBlockEntity.NodeDetector());
		}
	}
	
	//calling this is WEIRD as fuck on forge due to shit parallel dispatch garbage
	public void registerDispenserBehaviors() {
		DispenserBlock.registerBehavior(IncItems.BOUND_ENDER_PEARL, IncItems.BOUND_ENDER_PEARL.new DispenseBehavior());
	}
	
	//Very commonly used helpers
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static ResourceLocation botaniaId(String path) {
		return new ResourceLocation(LibMisc.MOD_ID, path);
	}
	
	public static <T> T choose(List<T> list, RandomSource random) {
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
