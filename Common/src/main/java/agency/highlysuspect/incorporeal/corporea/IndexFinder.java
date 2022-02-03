package agency.highlysuspect.incorporeal.corporea;

import agency.highlysuspect.incorporeal.mixin.TileCorporeaIndexAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;
import vazkii.botania.common.helper.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds Corporea Indices near a particular block.
 * Regular Botania utilities are only about finding corporea indices near a Player.
 */
public class IndexFinder {
	//TileCorporeaIndex#isInRangeOfIndex only accepts a Player argument
	public static List<TileCorporeaIndex> findNearBlock(Level level, BlockPos pos, int radius) {
		List<TileCorporeaIndex> result = new ArrayList<>();
		for(TileCorporeaIndex index : TileCorporeaIndexAccessor.inc$getServerIndices()) {
			//exists in a level
			if(index.getLevel() == null) continue;
			
			//exists in *this* level (because serverIndexes is a list of all blockentities ever in the world)
			Level indexLevel = index.getLevel();
			if(!indexLevel.dimension().equals(level.dimension())) continue;
			
			//MathHelper.pointDistancePlane(index.getBlockPos().getX() + 0.5, index.getBlockPos().getZ() + 0.5, player.getX(), player.getZ()) < RADIUS && Math.abs(index.getBlockPos().getY() + 0.5 - player.getY() + (player.level.isClientSide ? 0 : 1.6)) < 5
			if(MathHelper.pointDistancePlane(index.getBlockPos().getX() + 0.5, index.getBlockPos().getZ() + 0.5, pos.getX(), pos.getZ()) < radius && Math.abs(index.getBlockPos().getY() + 0.5 - pos.getY()) < 5) {
				result.add(index);
			}
		}
		
		return result;
	}
}
