package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.platform.IncXplat;
import agency.highlysuspect.incorporeal.platform.fabric.mixin.FabricAccessorDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;

public class IncXplatFabric implements IncXplat {
	@Override
	public Item.Properties defaultItemProperties() {
		return new Item.Properties().tab(IncFabricCreativeTab.INSTANCE);
	}
	
	@Override
	public DamageSource newDamageSource(String name) {
		return FabricAccessorDamageSource.inc$new(name);
	}
}
