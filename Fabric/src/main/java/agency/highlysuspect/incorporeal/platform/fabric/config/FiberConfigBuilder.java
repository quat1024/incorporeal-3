package agency.highlysuspect.incorporeal.platform.fabric.config;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.platform.ConfigBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

public class FiberConfigBuilder implements ConfigBuilder {
	public FiberConfigBuilder(String filename) {
		this.filename = filename;
		this.builder = ConfigTree.builder();
	}
	
	private final String filename;
	private ConfigTreeBuilder builder;
	
	@Override
	public void pushCategory(String name) {
		builder = builder.fork(name);
	}
	
	@Override
	public void popCategory() {
		builder = builder.finishBranch();
	}
	
	@Override
	public Supplier<Boolean> addBooleanProperty(String name, boolean defaultValue, String... commentLines) {
		//create the property mirror
		PropertyMirror<Boolean> boolMirror = PropertyMirror.create(ConfigTypes.BOOLEAN);
		
		//create a property and bind it to the mirror
		builder = builder.beginValue(name, ConfigTypes.BOOLEAN, defaultValue)
			.withComment(String.join(System.lineSeparator(), commentLines))
			.finishValue(boolMirror::mirror);
		
		//return the mirror's getter
		return boolMirror::getValue;
	}
	
	@Override
	public void finish() {
		setupConfig(builder.build(), Paths.get("config", filename + ".json5"), new JanksonValueSerializer(false));
	}
	
	/// Copypaste of private methods from Botania, using my logger of course; inlined a few things, blah blah... ///
	
	private static void setupConfig(ConfigTree config, Path p, JanksonValueSerializer serializer) {
		//It's no one mod's responsibility to create the configuration directory in this world.
		try {
			Files.createDirectory(Paths.get("config"));
		} catch (FileAlreadyExistsException ignored) {
			//Someone else did it! Great.
		} catch (IOException e) {
			Inc.LOGGER.warn("Failed to make config dir", e);
		}
		
		//Write the default config...
		try (OutputStream s = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
			FiberSerialization.serialize(config, s, serializer);
		} catch (FileAlreadyExistsException ignored) {
			//...but don't overwrite any existing config file
		} catch (IOException e) {
			Inc.LOGGER.error("Error writing default config", e);
		}
		
		//Read the config values out of the file
		try (InputStream s = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
			FiberSerialization.deserialize(config, s, serializer);
		} catch (IOException | ValueDeserializationException e) {
			Inc.LOGGER.error("Error loading config from {}", p, e);
		}
	}
}
