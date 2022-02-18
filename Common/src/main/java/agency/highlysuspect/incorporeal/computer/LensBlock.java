package agency.highlysuspect.incorporeal.computer;

import net.minecraft.world.level.block.Block;

public class LensBlock extends Block {
	public LensBlock(DataLens lens, Properties $$0) {
		super($$0);
		this.lens = lens;
	}
	
	public final DataLens lens;
}
