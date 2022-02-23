package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncSounds;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.corporea.PlayerHeadHandler;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import vazkii.botania.api.corporea.CorporeaIndexRequestCallback;

public class IncFabric implements ModInitializer {
	public static final ResourceLocation NETWORK_ID = new ResourceLocation(Inc.MODID, "net");
	
	@Override
	public void onInitialize() {
		//blocks
		IncBlocks.register((block, name) -> Registry.register(Registry.BLOCK, name, block));
		
		//block entity types
		IncBlockEntityTypes.register((type, name) -> Registry.register(Registry.BLOCK_ENTITY_TYPE, name, type));
		
		//items
		IncItems.register((item, name) -> Registry.register(Registry.ITEM, name, item));
		
		//sound events
		IncSounds.register((sound, name) -> Registry.register(Registry.SOUND_EVENT, name, sound));
		
		//that one corporea event
		CorporeaIndexRequestCallback.EVENT.register(PlayerHeadHandler::onIndexRequest);
		
		//some other stuff (not different between loaders)
		Inc.registerExtraThings();
	}
}
