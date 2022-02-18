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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
	
	/**
	 * ("Thing which can be written as json", "list of strings") tuple.
	 * First path segment is the jar root, so probably "assets" or "data"
	 * 
	 * passing ("data", "incorporeal", "recipes", "blah") will write to data/incorporeal/recipes/blah.json.
	 * investigate: could this be a (JValue<?>, Path) tuple instead? idk
	 */
	public static record JsonFile(JValue<?> value, List<String> pathSegments) {
		public static JsonFile create(JValue<?> value, String... pathSegments) {
			return new JsonFile(value, List.of(pathSegments));
		}
		
		public static JsonFile coerce(Object value, String... pathSegments) {
			return new JsonFile(JValue.coerce(value), List.of(pathSegments));
		}
		
		public Path getOutputPath(DataGenerator datagen) {
			Path path = datagen.getOutputFolder();
			
			//grimy code to concatenate all path segments with a .json on the end
			//there's not like a "Path#withFileName" or anything >.>
			//probably could be cleaner
			for(int i = 0; i < pathSegments.size(); i++) {
				if(i == pathSegments.size() - 1) path = path.resolve(pathSegments.get(i) + ".json");
				else path = path.resolve(pathSegments.get(i));
			}
			
			return path;
		}
		
		//wrapper for DataProvider#save that swallows errors
		//mainly so you don't have to slap "throws IOException" on the whole mod
		//and so you can use them inside lambdas (for the same reason)
		public void save(DataGenerator datagen, HashCache cache) {
			try {
				DataProvider.save(PRETTY_GSON, cache, value.toGson(), getOutputPath(datagen));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * "Thing that can be serialized as a gson JsonElement"
	 */
	public interface JValue<T extends JsonElement> {
		T toGson();
		
		/**
		 * Tries to guess what json type you mean by a particular Java object.
		 * Like if you pass an integer, you probably want a json number.
		 * Anything already a JValue gets returned as-is.
		 * Aka: shitty version of GSON's type adapter thingie.
		 */
		static JValue<?> coerce(Object o) {
			//already jvalues
			if(o instanceof JValue<?> val) return val;
			//resourcelocations - also tries to catch usages of "minecraft:air"
			if(o instanceof ResourceLocation rl) return new JSimple(new JsonPrimitive(DataDsl.notAir(rl).toString()));
			//google gson primitives
			if(o instanceof Integer i) return new JSimple(new JsonPrimitive(i));
			if(o instanceof Boolean b) return new JSimple(new JsonPrimitive(b));
			if(o instanceof String s) return new JSimple(new JsonPrimitive(s));
			if(o instanceof JsonObject x) return new JSimple(x);
			//various registry items (recurses up into the ResourceLocation case)
			if(o instanceof Item i) return coerce(Registry.ITEM.getKey(i));
			if(o instanceof Block b) return coerce(Registry.BLOCK.getKey(b));
			if(o instanceof ItemLike il && il.asItem() != Items.AIR) return coerce(il.asItem());
			
			//(toss some more things in here as needed)
			
			return new JSimple(new JsonPrimitive(o.toString())); //hope toString works on it i guess lol
		}
		
		default JsonFile fileOf(String... segments) {
			return JsonFile.coerce(this, segments);
		}
	}
	
	public static record JSimple(JsonElement elem) implements JValue<JsonElement> {
		@Override
		public JsonElement toGson() {
			return elem;
		}
	}
	
	//Builders for json objects & json arrays
	//much nicer to use (when manually constructing large JSON objects) than google GSON
	
	/**
	 * { ... }
	 */
	public static record JObject(Map<String, JValue<?>> values) implements JValue<JsonObject> {
		public JObject() {
			this(new TreeMap<>());
		}
		
		@Override
		public JsonObject toGson() {
			JsonObject yes = new JsonObject();
			values.forEach((k, v) -> yes.add(k, v.toGson()));
			return yes;
		}
		
		/**
		 * Accepts an alternating sequence of keys and values.
		 * Keys coerced to strings with toString, values coerced to JValues using JValue.coerce.
		 */
		public JObject fields(Object... keyValues) {
			Iterator<Object> objerator = List.of(keyValues).iterator();
			while(objerator.hasNext()) {
				String key = objerator.next().toString();
				Object value = objerator.next();
				values.put(key, JValue.coerce(value));
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
		
		/**
		 * Values coerced to JValues using JValue.coerce.
		 */
		public JArray values(Object... values) {
			this.values.addAll(Stream.of(values).map(JValue::coerce).collect(Collectors.toList()));
			return this;
		}
	}
}