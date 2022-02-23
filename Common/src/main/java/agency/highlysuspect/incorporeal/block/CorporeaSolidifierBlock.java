package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import vazkii.botania.common.helper.InventoryHelper;
import vazkii.botania.xplat.IXplatAbstractions;

import javax.annotation.Nullable;

/**
 * A block that kinda acts like a corporea retainer, but spits its requests out as corporea ticket items, instead of storing them.
 * 
 * @see agency.highlysuspect.incorporeal.mixin.TileCorporeaInterceptorMixin for the sauce that makes this actually work.
 *
 * This block, on its own, does very little. It's a convenient place to stick the code relating to spawning the corporea ticket, though.
 */
public class CorporeaSolidifierBlock extends Block {
	public CorporeaSolidifierBlock(Properties props) {
		super(props);
	}
	
	public void receiveRequest(Level level, BlockPos pos, SolidifiedRequest request) {
		if(level == null || level.isClientSide()) return;
		
		//Based on copy and paste from TileCorporeaFunnel
		
		ItemStack ticket = request.toTicket();
		BlockPos invPos = getInvPos(level, pos);
		if (invPos != null
			&& IXplatAbstractions.INSTANCE.insertToInventory(level, invPos, Direction.UP, ticket, true).isEmpty()) {
			InventoryHelper.checkEmpty(
				IXplatAbstractions.INSTANCE.insertToInventory(level, invPos, Direction.UP, ticket, false)
			);
		} else {
			//Corporea funnel does this:
			//ItemEntity item = new ItemEntity(level, spark.entity().getX(), spark.entity().getY(), spark.entity().getZ(), reqStack);
			//I'm not sparked though, so I'll spawn the item in the same place as a spark would be, 1.25 blocks up
			ItemEntity item = new ItemEntity(level, pos.getX() + .5, pos.getY() + 1.25, pos.getZ() + .5, ticket);
			level.addFreshEntity(item);
		}
	}
	
	//Based on copy and paste from TileCorporeaFunnel
	@Nullable
	private BlockPos getInvPos(Level level, BlockPos worldPosition) {
		BlockPos downOne = worldPosition.below();
		if (IXplatAbstractions.INSTANCE.hasInventory(level, downOne, Direction.UP)) {
			return downOne;
		}
		
		BlockPos downTwo = worldPosition.below(2);
		if (IXplatAbstractions.INSTANCE.hasInventory(level, downTwo, Direction.UP)) {
			return downTwo;
		}
		
		return null;
	}
}
