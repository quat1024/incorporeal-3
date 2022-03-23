package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer {
	@Override
	public void onInitialize() {
		Inc.INSTANCE.onInitialize();
	}
}
