package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

public class IncFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		IncBlocks.register((block, name) -> Registry.register(Registry.BLOCK, name, block));
		IncBlockEntityTypes.register((type, name) -> Registry.register(Registry.BLOCK_ENTITY_TYPE, name, type));
		IncItems.register((item, name) -> Registry.register(Registry.ITEM, name, item));
		
		Inc.registerExtraThings();
	}
}
