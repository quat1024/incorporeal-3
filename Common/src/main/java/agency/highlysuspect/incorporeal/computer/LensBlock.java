package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.computer.types.DataLens;
import net.minecraft.world.level.block.Block;

public class LensBlock extends Block {
	public LensBlock(DataLens lens, Properties props) {
		super(props);
		this.lens = lens;
	}
	
	public final DataLens lens;
}
