package agency.highlysuspect.incorporeal.platform.fabric.client;

import agency.highlysuspect.incorporeal.client.IncClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricClientEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		IncClient.INSTANCE.onInitializeClient();
	}
}
