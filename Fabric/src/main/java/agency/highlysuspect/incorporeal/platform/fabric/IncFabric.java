package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.IncItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

public class IncFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Inc.init();
		
		IncBlocks.register((block, name) -> Registry.register(Registry.BLOCK, name, block));
		IncItems.register((item, name) -> Registry.register(Registry.ITEM, name, item));
	}
}
