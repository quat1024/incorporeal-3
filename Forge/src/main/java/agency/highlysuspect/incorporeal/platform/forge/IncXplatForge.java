package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;

public class IncXplatForge implements IncXplat {
	@Override
	public Item.Properties defaultItemProperties() {
		return new Item.Properties().tab(IncForgeCreativeTab.INSTANCE);
	}
	
	@Override
	public DamageSource newDamageSource(String name) {
		return new DamageSource(name);
	}
}
