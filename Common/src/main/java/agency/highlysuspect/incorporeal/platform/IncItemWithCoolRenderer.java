package agency.highlysuspect.incorporeal.platform;

import net.minecraft.world.item.Item;

/**
 * Mixed in from incorporeal-forge because forge has to be a special boy and have a weird item renderer API.
 */
public class IncItemWithCoolRenderer extends Item {
	public IncItemWithCoolRenderer(Properties props) {
		super(props);
	}
}
