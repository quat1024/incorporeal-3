package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * ("Json", "list of strings") tuple.
 * First path segment is the jar root, so probably "assets" or "data" -
 * passing ("data", "incorporeal", "recipes", "blah") will write to data/incorporeal/recipes/blah.json.
 * investigate: could this be a (JsonElement, Path) tuple instead? Idk
 */
public record JsonFile(JsonElement value, List<String> pathSegments) {
	public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static JsonFile create(JsonElement value, String... pathSegments) {
		return new JsonFile(value, List.of(pathSegments));
	}
	
	public Path getOutputPath(DataGenerator datagen) {
		Path path = datagen.getOutputFolder();
		
		//Grimy code to concatenate all path segments with a .json at the end
		//There's not like a "Path#withFileName" or anything >.> probably could be cleaner
		for(int i = 0; i < pathSegments.size(); i++) {
			if(i == pathSegments.size() - 1) path = path.resolve(pathSegments.get(i) + ".json");
			else path = path.resolve(pathSegments.get(i));
		}
		
		return path;
	}
	
	//Wrapper for DataProvider#save that doesn't throw IOException, mainly so you don't have to slap "throws IOException" on the whole mod
	//and so you can use them inside lambdas (for the same reason)
	public void save(DataGenerator datagen, CachedOutput cache) {
		try {
			// TODO 1.19 figure out why this doesnt take the GSON and figure out how to make it
			DataProvider.saveStable(cache, value, getOutputPath(datagen));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
