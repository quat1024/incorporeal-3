package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import vazkii.botania.common.block.tile.corporea.TileCorporeaFunnel;
import vazkii.botania.common.helper.InventoryHelper;

/**
 * A block that kinda acts like a corporea retainer, but spits its requests out as corporea ticket items, instead of storing them.
 */
public class CorporeaSolidifierBlock extends Block {
	public CorporeaSolidifierBlock(Properties props) {
		super(props);
	}
	
	//Called via TileCorporeaInterceptorMixin
	public void receiveRequest(Level level, BlockPos pos, SolidifiedRequest request) {
		if(level == null || level.isClientSide()) return;
		
		ItemStack ticket = request.toTicket();
		Container inv = getInv(level, pos);
		
		//Copy and paste from TileCorporeaFunnel
		if (inv != null && InventoryHelper.simulateTransfer(inv, ticket, Direction.UP).isEmpty()) {
			HopperBlockEntity.addItem(null, inv, ticket, Direction.UP);
		} else {
			ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, ticket);
			level.addFreshEntity(item);
		}
	}
	
	//Copy and paste from TileCorporeaFunnel
	private Container getInv(Level level, BlockPos worldPosition) {
		BlockEntity te = level.getBlockEntity(worldPosition.below());
		Container ret = InventoryHelper.getInventory(level, worldPosition.below(), Direction.UP);
		if (ret == null) {
			ret = InventoryHelper.getInventory(level, worldPosition.below(), null);
		}
		if (ret != null && !(te instanceof TileCorporeaFunnel)) {
			return ret;
		}
		
		te = level.getBlockEntity(worldPosition.below(2));
		ret = InventoryHelper.getInventory(level, worldPosition.below(2), Direction.UP);
		if (ret == null) {
			ret = InventoryHelper.getInventory(level, worldPosition.below(2), null);
		}
		if (ret != null && !(te instanceof TileCorporeaFunnel)) {
			return ret;
		}
		
		return null;
	}
}
