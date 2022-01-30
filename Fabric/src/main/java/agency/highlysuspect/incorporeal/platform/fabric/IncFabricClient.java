package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.client.IncItemProperties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

public class IncFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		IncItemProperties.register((item, id, prop) -> FabricModelPredicateProviderRegistry.register(item.asItem(), id, prop));
	}
}
