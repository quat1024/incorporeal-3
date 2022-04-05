package agency.highlysuspect.incorporeal.platform.forge.config;

import agency.highlysuspect.incorporeal.platform.ConfigBuilder;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Supplier;

public class ForgeConfigBuilder implements ConfigBuilder {
	public ForgeConfigBuilder(String filename) {
		this.filename = filename;
		this.builder = new ForgeConfigSpec.Builder();
	}
	
	private final String filename;
	private ForgeConfigSpec.Builder builder;
	
	@Override
	public void pushCategory(String name) {
		builder = builder.push(name);
	}
	
	@Override
	public void popCategory() {
		builder = builder.pop();
	}
	
	@Override
	public Supplier<Boolean> addBooleanProperty(String name, boolean defaultValue, String... commentLines) {
		//create a property mirror bound to this property
		ForgeConfigSpec.BooleanValue boolValue = builder.comment(commentLines).define(name, defaultValue);
		
		//return the mirror's getter
		return boolValue::get;
	}
	
	@Override
	public void finish() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build(), filename + ".toml");
	}
}
