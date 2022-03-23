package agency.highlysuspect.incorporeal.platform;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * Mixed in from incorporeal-forge because forge has to be a special boy and have a weird item renderer API.
 */
public class IncBlockItemWithCoolRenderer extends BlockItem {
	public IncBlockItemWithCoolRenderer(Block block, Properties props) {
		super(block, props);
	}
}
