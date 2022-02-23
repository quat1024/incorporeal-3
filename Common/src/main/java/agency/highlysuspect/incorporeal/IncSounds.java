package agency.highlysuspect.incorporeal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.BiConsumer;

public class IncSounds {
	public static final SoundEvent UNSTABLE = new SoundEvent(Inc.id("unstable"));
	
	public static void register(BiConsumer<SoundEvent, ResourceLocation> r) {
		r.accept(UNSTABLE, UNSTABLE.getLocation());
	}
}
