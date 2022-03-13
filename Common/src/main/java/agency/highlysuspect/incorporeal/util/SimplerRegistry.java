package agency.highlysuspect.incorporeal.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * The last thing I want to do is buy into Mojang's registry system right before 1.18.2.
 * I don't need many of the features that Registries have anyways; syncing, or stability versioning, or whatever the hell else.
 * All I need to do is name things with a ResourceLocation so I can load them off disk later, or whatever.
 * 
 * Name inspired from Yarn's name of "SimpleRegistry" for a class that was... not simple. ;)
 */
public class SimplerRegistry<T> {
	public final Map<T, ResourceLocation> thingsToIds = new IdentityHashMap<>();
	public final Map<ResourceLocation, T> idsToThings = new HashMap<>();
	
	//Marked as synchronized because, like, something something forge parallel loading, lmao.
	public synchronized T register(T thing, ResourceLocation id) {
		thingsToIds.put(thing, id);
		idsToThings.put(id, thing);
		return thing;
	}
	
	public ResourceLocation getKey(T thing) {
		return thingsToIds.get(thing);
	}
	
	public T get(@Nullable ResourceLocation id) {
		return idsToThings.get(id);
	}
}

//Well damn! That's all you need to do to implement a registry? Wild!
