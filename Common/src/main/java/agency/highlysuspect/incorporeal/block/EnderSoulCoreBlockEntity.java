package agency.highlysuspect.incorporeal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EnderSoulCoreBlockEntity extends AbstractSoulCoreBlockEntity {
	public EnderSoulCoreBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.ENDER_SOUL_CORE, pos, state);
	}
	
	@Override
	protected int getMaxMana() {
		return 5000;
	}
	
	@Override
	protected void tick() {
		//Stub
	}
}
