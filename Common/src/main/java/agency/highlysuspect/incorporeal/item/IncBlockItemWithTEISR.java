package agency.highlysuspect.incorporeal.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * As with botania-forge's ItemBlockWithSpecialRenderer, mixed in from incorporeal-forge
 * because forge has to be a special boy and do crappy shit with item renderers.
 * 
 * Does nothing on Fabric, over there use BuiltinItemRendererRegistry like a civilized modloader.
 */
public class IncBlockItemWithTEISR extends BlockItem {
	public IncBlockItemWithTEISR(Block block, Properties props) {
		super(block, props);
	}
}
