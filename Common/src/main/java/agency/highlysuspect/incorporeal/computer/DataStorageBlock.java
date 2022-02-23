package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DataStorageBlock extends Block implements EntityBlock {
	public DataStorageBlock(Properties $$0) {
		super($$0);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IncBlockEntityTypes.DATA_STORAGE.create(pos, state);
	}
}
