package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * > As far as I know, for a given language, only one language JSON can exist per resource pack.
 * > This poses a problem because I'd like to use datagen to generate language entries, but I don't want to stomp on
 * > manual additions to the en_us.json file either.
 * <p>
 * Nope! you can just put it in a different namespace. We no longer need a DataGeneratorMixin. We just put an instance
 * of this class at the top of PatchouliEntryBuilder and call write at the end of the lexicon generator.
 */
public class LangWriter {
	private final String namespace;

	private final String langFile;

	public LangWriter(String namespace, String langFile) {
		this.namespace = namespace;
		this.langFile = langFile;
	}

	private final Map<String, String> entries = new LinkedHashMap<>(); //preserve ordering

	public void associate(String key, String value) {
		entries.put(key, value);
	}

	public void write(Consumer<JsonFile> files) {
		JsonObject json = new JsonObject();
		for (Map.Entry<String, String> entry : entries.entrySet()) {
			json.addProperty(entry.getKey(), entry.getValue());
		}
		files.accept(JsonFile.create(json, "assets", namespace, "lang", langFile));
	}
}
