package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.world.item.Item;

public class IncXplatFabric implements IncXplat {
	@Override
	public Item.Properties defaultItemProperties() {
		return new Item.Properties().tab(IncFabricCreativeTab.INSTANCE);
	}
}
