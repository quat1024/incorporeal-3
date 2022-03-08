package agency.highlysuspect.incorporeal.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import vazkii.botania.common.block.tile.string.TileRedString;
import vazkii.botania.xplat.IXplatAbstractions;

public class RedStringConstrictorBlockEntity extends TileRedString {
	public RedStringConstrictorBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.RED_STRING_CONSTRICTOR, pos, state);
	}
	
	public boolean removesSlotsFromFront() {
		return !getBlockState().getValue(BlockStateProperties.INVERTED);
	}
	
	public int removedSlotCount() {
		return getBlockState().getValue(BlockStateProperties.POWER);
	}
	
	//TODO: allow binding these to each other (replace commontick with my own)
	
	@Override
	public boolean acceptBlock(BlockPos pos) {
		assert level != null;
		
		BlockEntity tile = level.getBlockEntity(pos);
		return tile != null && IXplatAbstractions.INSTANCE.isRedStringContainerTarget(tile);
	}
}
