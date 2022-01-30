package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.corporea.FrameReader;
import agency.highlysuspect.incorporeal.corporea.IncInventoryHelper;
import agency.highlysuspect.incorporeal.corporea.LyingContainerCorporeaNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.ICorporeaNode;
import vazkii.botania.api.corporea.ICorporeaNodeDetector;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.block.tile.string.TileRedString;
import vazkii.botania.common.impl.corporea.DummyCorporeaNode;

public class RedStringLiarBlockEntity extends TileRedString {
	public RedStringLiarBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.RED_STRING_LIAR, pos, state);
	}
	
	@Override
	public boolean acceptBlock(BlockPos pos) {
		//TODO: I'd like the Red String Liar to be able to interact with anything capable of emitting a corporea node.
		// But CorporeaNodeDetectors.findNode requires a corporea spark, and it's just not possible to call that without
		// a spark like I need to do here. Additionally in getNode, when I need to retrieve the target's corporea node in
		// order to wrap it, I can't, because the only ICorporeaSpark I can pass into it is the one above the Red String
		// Liar itself.
		
		//In the meantime, allow binding to any Container I guess.
		return getContainer(pos) != null;
	}
	
	private Container getContainer(@NotNull BlockPos pos) {
		assert level != null;
		return IncInventoryHelper.getSidedContainerAt(level, pos, getOrientation().getOpposite());
	}
	
	private @Nullable Container getBoundContainer() {
		if(getBinding() == null) return null;
		else return getContainer(getBinding());
	}
	
	private @NotNull ICorporeaNode getNode(Level level, ICorporeaSpark spark) {
		Container container = getBoundContainer();
		if(container != null) {
			return new LyingContainerCorporeaNode(
				level, spark.getAttachPos(), spark,
				container,
				FrameReader.nonEmptyItemsRestingOn(level, worldPosition)
			);
		}
		
		return new DummyCorporeaNode(level, spark.getAttachPos(), spark);
	}
	
	public static class NodeDetector implements ICorporeaNodeDetector {
		@Nullable
		@Override
		public ICorporeaNode getNode(Level level, ICorporeaSpark spark) {
			BlockEntity be = level.getBlockEntity(spark.getAttachPos());
			if(be instanceof RedStringLiarBlockEntity rsl) return rsl.getNode(level, spark);
			else return null;
		}
	}
}
