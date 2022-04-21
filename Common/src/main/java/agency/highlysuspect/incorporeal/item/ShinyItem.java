package agency.highlysuspect.incorporeal.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShinyItem extends Item {
	public ShinyItem(Properties props) {
		super(props);
	}
	
	@Override
	public boolean isFoil(ItemStack $$0) {
		return true;
	}
}
