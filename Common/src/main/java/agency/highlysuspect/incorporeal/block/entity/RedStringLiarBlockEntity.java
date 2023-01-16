package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.corporea.FrameReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.CorporeaNode;
import vazkii.botania.api.corporea.CorporeaNodeDetector;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.common.block.block_entity.red_string.RedStringBlockEntity;

import java.util.List;

/**
 * The Red String Liar. It exposes an inventory to the Corporea Network as if it contained different items.
 */
public abstract class RedStringLiarBlockEntity extends RedStringBlockEntity {
	public RedStringLiarBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.RED_STRING_LIAR, pos, state);
	}
	
	//Forwarding from TileRedString
	@Override
	public abstract boolean acceptBlock(BlockPos pos);
	public abstract @NotNull CorporeaNode createCorporeaNode(CorporeaSpark spark);
	
	public List<ItemStack> readSpoofStacks() {
		assert level != null;
		return FrameReader.nonEmptyItemsRestingOn(level, worldPosition);
	}
	
	public static class NodeDetector implements CorporeaNodeDetector {
		@Nullable
		@Override
		public CorporeaNode getNode(Level level, CorporeaSpark spark) {
			BlockEntity be = level.getBlockEntity(spark.getAttachPos());
			if(be instanceof RedStringLiarBlockEntity rsl) return rsl.createCorporeaNode(spark);
			else return null;
		}
	}
}
