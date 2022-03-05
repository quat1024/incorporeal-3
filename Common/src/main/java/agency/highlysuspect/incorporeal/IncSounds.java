package agency.highlysuspect.incorporeal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.BiConsumer;

public class IncSounds {
	public static final SoundEvent UNSTABLE = new SoundEvent(Inc.id("unstable"));
	public static final SoundEvent RETAINER_EVAPORATOR_CLICK = new SoundEvent(Inc.id("retainer_evaporator_click"));
	
	public static void register(BiConsumer<SoundEvent, ResourceLocation> r) {
		registerSoundEvents(r,
			UNSTABLE, RETAINER_EVAPORATOR_CLICK
		);
	}
	
	private static void registerSoundEvents(BiConsumer<SoundEvent, ResourceLocation> r, SoundEvent... events) {
		for(SoundEvent event : events) r.accept(event, event.getLocation());
	}
}
