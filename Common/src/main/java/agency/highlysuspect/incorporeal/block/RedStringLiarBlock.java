package agency.highlysuspect.incorporeal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.string.BlockRedString;
import vazkii.botania.common.block.tile.string.TileRedString;

/**
 * The Red String Liar.
 */
public class RedStringLiarBlock extends BlockRedString {
	public RedStringLiarBlock(Properties props) {
		super(props);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IncBlockEntityTypes.RED_STRING_LIAR.create(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, IncBlockEntityTypes.RED_STRING_LIAR, TileRedString::commonTick);
	}
}
