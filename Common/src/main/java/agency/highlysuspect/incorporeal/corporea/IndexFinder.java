package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.common.helper.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds Corporea Indices near a particular block.
 * Regular Botania utilities are only about finding corporea indices near a Player, for... pretty obvious reasons.
 */
public class IndexFinder {
	//Radius rounded up to a full block.
	public static int PESSIMISTIC_RADIUS = Mth.ceil(CorporeaIndexBlockEntity.RADIUS);
	public static int HEIGHT = 5;
	
	public static List<CorporeaIndexBlockEntity> findNearBlock(Level level, BlockPos pos) {
		List<CorporeaIndexBlockEntity> result = new ArrayList<>();
		
		BlockPos low = pos.offset(-PESSIMISTIC_RADIUS, -HEIGHT, -PESSIMISTIC_RADIUS);
		BlockPos high = pos.offset(PESSIMISTIC_RADIUS, HEIGHT, PESSIMISTIC_RADIUS);
		
		for(BlockPos tryPos : BlockPos.betweenClosed(low, high)) {
			//Similar check that TileCorporeaIndex#isInRangeOfIndex uses.
			//Here, I don't use a Y-level check, because I only iterate through Y-levels that are close enough in the first place?
			//Hmmmm....
			if(MathHelper.pointDistancePlane(tryPos.getX(), tryPos.getZ(), pos.getX(), pos.getZ()) > CorporeaIndexBlockEntity.RADIUS) continue;
			
			CorporeaIndexBlockEntity index = BotaniaBlockEntities.CORPOREA_INDEX.getBlockEntity(level, tryPos);
			if(index != null) result.add(index);
		}
		
		return result;
	}
}
