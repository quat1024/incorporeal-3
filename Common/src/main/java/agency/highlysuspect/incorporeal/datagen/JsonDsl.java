package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonDsl {
	public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static JObject obj(Object... fields) {
		return new JObject().fields(fields);
	}
	
	public static JArray array(Object... values) {
		return new JArray().values(values);
	}
	
	//make extra sure i'm actually flattening the array out instead
	//of passing it as one argument to a varargs method (kinda sloppy java)
	public static JArray array0(Object[] values) {
		JArray j = new JArray();
		for(Object value : values) j.value(value);
		return j;
	}
	
	public static JObject.Field field(String name, Object value) {
		return JObject.Field.coerce(name, value);
	}
	
	/**
	 * for example, the path
	 * - src/generated/resources/data/incorporeal/loot_tables/blocks/clearly.json:
	 * 
	 * has:
	 * classifier - data
	 * namespace - incorporeal
	 * category - loot_tables/blocks
	 * fileName - clearly
	 */
	public static record JsonFile(String classifier, String namespace, String category, String fileName, JValue<?> value) {
		public Path getOutputPath(DataGenerator datagen) {
			return datagen.getOutputFolder().resolve(classifier).resolve(namespace).resolve(category).resolve(fileName + ".json");
		}
		
		public void save(DataGenerator datagen, HashCache cache) {
			try {
				DataProvider.save(PRETTY_GSON, cache, value.toGson(), getOutputPath(datagen));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Thin wrapper around google gson
	 */
	public interface JValue<T extends JsonElement> {
		T toGson();
		
		/**
		 * Tries to guess what json type you mean by a particular Java object
		 * like if you pass an integer, you probably want a json number
		 * Anything already a JValue gets returned as-is
		 */
		static JValue<?> coerce(Object o) {
			if(o instanceof JValue<?> val) return val;
			if(o instanceof Integer i) return new JSimple(new JsonPrimitive(i));
			if(o instanceof Boolean b) return new JSimple(new JsonPrimitive(b));
			if(o instanceof Item i) return coerce(Registry.ITEM.getKey(i));
			if(o instanceof Block b) return coerce(Registry.BLOCK.getKey(b));
			if(o instanceof ItemLike il && il.asItem() != Items.AIR) return coerce(il.asItem());
			//toss some more things in here later
			
			return new JSimple(new JsonPrimitive(o.toString())); //hope toString works
		}
		
		default JsonFile fileOf(String classifier, String namespace, String category, String fileName) {
			return new JsonFile(classifier, namespace, category, fileName, this);
		}
		
		default String prettyPrint() {
			return PRETTY_GSON.toJson(toGson());
		}
	}
	
	public static record JSimple(JsonElement elem) implements JValue<JsonElement> {
		@Override
		public JsonElement toGson() {
			return elem;
		}
	}
	
	/**
	 * { ... }
	 */
	public static record JObject(Map<String, JValue<?>> values) implements JValue<JsonObject> {
		public JObject() {
			this(new HashMap<>());
		}
		
		@Override
		public JsonObject toGson() {
			JsonObject yes = new JsonObject();
			values.forEach((k, v) -> yes.add(k, v.toGson()));
			return yes;
		}
		
		public static record Field(String key, JValue<?> value) {
			public static Field coerce(String key, Object o) {
				return new Field(key, JValue.coerce(o));
			}
		}
		
		/**
		 * Accepts an alternating sequence of keys and values.
		 * Keys must be strings, other things are coerced to JValues using JValue.coerce.
		 * Or you can pass a JObject.Field, which counts as both a key and a value
		 */
		public JObject fields(Object... keyValues) {
			Iterator<Object> objerator = List.of(keyValues).iterator();
			while(objerator.hasNext()) {
				Object key = objerator.next();
				
				if(key instanceof Field f) {
					values.put(f.key, f.value);
					continue;
				}
				
				if(!(key instanceof String keyString)) {
					throw new IllegalArgumentException("key is not a string");
				}
				
				Object value = objerator.next();
				values.put(keyString, JValue.coerce(value));
			}
			return this;
		}
	}
	
	/**
	 * [ ... ]
	 */
	public static record JArray(List<JValue<?>> values) implements JValue<JsonArray> {
		public JArray() {
			this(new ArrayList<>());
		}
		
		@Override
		public JsonArray toGson() {
			JsonArray yes = new JsonArray();
			values.forEach(v -> yes.add(v.toGson()));
			return yes;
		}
		
		public JArray value(Object value) {
			this.values.add(JValue.coerce(value));
			return this;
		}
		
		public JArray values(Object... values) {
			this.values.addAll(Stream.of(values).map(JValue::coerce).collect(Collectors.toList()));
			return this;
		}
	}
}