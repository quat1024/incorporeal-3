package agency.highlysuspect.incorporeal.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ContainerUtil {
	//Based on copy-paste from HopperBlockEntity#getContainerAt, but doesn't fall through to a getEntitiesWithinAABB call.
	public static @Nullable Container getBlockContainerAt(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		if(block instanceof WorldlyContainerHolder wch) {
			return wch.getContainer(state, level, pos);
		} else if(state.hasBlockEntity()) {
			BlockEntity be = level.getBlockEntity(pos);
			if(!(be instanceof Container cont)) return null;
			
			if(cont instanceof ChestBlockEntity && block instanceof ChestBlock chest) return ChestBlock.getContainer(chest, state, level, pos, true);
			else return cont;
		}
		
		return null;
	}
}
