package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * As far as I know, for a given language, only one language JSON can exist per resource pack.
 * This poses a problem because I'd like to use datagen to generate language entries, but I don't want to stomp on
 * manual additions to the en_us.json file either.
 * 
 * As a compromise, I allow the data generator to directly edit src/main/resources/.../en_us.json, instead
 * of generating its own lang file. It works like this:
 * - read en_us.json,
 * - search for the string "en-us-rewriter.marker" in the file,
 * - erase everything below that line,
 * - splice in the generated language entries.
 * 
 * It is made accessible to data generators through DatagenDuck/DataGeneratorMixin.
 */
public class EnUsRewriter {
	private static final String DELIMITER = "en-us-rewriter.marker";
	private final Map<String, String> entries = new LinkedHashMap<>(); //preserve ordering
	
	public void associate(String key, String value) {
		entries.put(key, value);
	}
	
	public void rewrite() {
		// temporarily turn off
//		if(entries.isEmpty()) return;
//		Inc.LOGGER.info("Rewriting en_us.json (appending {} entries)", entries.size());
//
//		String systemProperty = System.getProperty("incorporeal.en-us"); //see :Fabric/build.gradle.kts
//		if(systemProperty == null || systemProperty.isEmpty()) {
//			Inc.LOGGER.warn("Cannot rewrite en_us.json - Did not set incorporeal.en-us system property");
//			return;
//		}
//		Path enUsPath = Paths.get(systemProperty);
//
//		try {
//			//Read the existing en_us file into a big string.
//			String enUs = Files.readString(enUsPath, StandardCharsets.UTF_8);
//
//			//Truncate the file after the line containing DELIMITER.
//			int delimiterIndex = enUs.indexOf(DELIMITER);
//			int newlineAfterDelim = enUs.indexOf('\n', delimiterIndex);
//			String truncated = enUs.substring(0, newlineAfterDelim + 1); //+1 to include the newline character in the prefix
//
//			//Splice in the new entries. This is simple string-gluing, it's not aware of anything jsonic.
//			String rewritten = truncated + entries.entrySet().stream()
//				.map(entry -> "\t\"" + escape(entry.getKey()) + "\": \"" + escape(entry.getValue()) + "\"")
//				.collect(Collectors.joining(",\n")) +
//				"\n}\n"; //And close the json file again
//
//			//Overwrite the original file.
//			Files.writeString(enUsPath, rewritten, StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	private static String escape(String in) {
		//not a proper json escape but gets the job done for now
		return in.replace("\"", "\\\"");
	}
}
