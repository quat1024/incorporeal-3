package agency.highlysuspect.incorporeal.platform.forge.block.entity;

import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.corporea.LyingCorporeaNode;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.corporea.CorporeaNode;
import vazkii.botania.api.corporea.CorporeaRequest;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.common.impl.corporea.DummyCorporeaNode;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;

/**
 * Implementation of the Red Stringed Liar on Forge.
 * Here it uses the platform's IItemHandler abstraction and capabilities.
 */
public class ForgeRedStringLiarBlockEntity extends RedStringLiarBlockEntity {
	public ForgeRedStringLiarBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
	
	@Override
	public boolean acceptBlock(BlockPos pos) {
		assert level != null;
		
		BlockEntity be = level.getBlockEntity(pos);
		return be != null && XplatAbstractions.INSTANCE.isRedStringContainerTarget(be); //out of laziness
	}
	
	@Override
	public @NotNull CorporeaNode createCorporeaNode(CorporeaSpark spark) {
		BlockEntity be = getTileAtBinding();
		if(be == null) return new DummyCorporeaNode(level, spark.getAttachPos(), spark);
		
		LazyOptional<IItemHandler> handlerOp = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		if(!handlerOp.isPresent()) return new DummyCorporeaNode(level, spark.getAttachPos(), spark);
		@SuppressWarnings("OptionalGetWithoutIsPresent")
		IItemHandler handler = handlerOp.resolve().get();
		
		return new Node(level, spark.getAttachPos(), spark, readSpoofStacks(), handler);
	}
	
	public static class Node extends LyingCorporeaNode {
		public Node(Level world, BlockPos pos, CorporeaSpark spark, List<ItemStack> spoofStacks, IItemHandler inv) {
			super(world, pos, spark, spoofStacks);
			this.inv = inv;
		}
		
		protected final IItemHandler inv;
		
		@Override
		protected int countItemsInInventory() {
			int howMany = 0;
			for(int i = 0; i < inv.getSlots(); i++) howMany += inv.getStackInSlot(i).getCount();
			return howMany;
		}
		
		//Based on copy from ForgeCapCorporeaNode
		//@formatter:off
		@Override
		protected List<ItemStack> extractEverything(CorporeaRequest request) {
			ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
			
			for(int i = this.inv.getSlots() - 1; i >= 0; --i) {
				ItemStack stackAt = this.inv.getStackInSlot(i);
				//if (request.getMatcher().test(stackAt)) { //Incorporeal: Do not test the request matcher
					request.trackFound(stackAt.getCount());
					int rem = Math.min(stackAt.getCount(), request.getStillNeeded() == -1 ? stackAt.getCount() : request.getStillNeeded());
					if (rem > 0) {
						request.trackSatisfied(rem);
						//if (doit) { //Incorporeal: remove dry-run functionality
							ItemStack copy = stackAt.copy();
							builder.addAll(breakDownBigStack(this.inv.extractItem(i, rem, this.getSpark().isCreative())));
							this.getSpark().onItemExtracted(copy);
							request.trackExtracted(rem);
						//} else {
						//	builder.add(this.inv.extractItem(i, rem, true));
						//}
					}
				//}
			}
			
			return builder.build();
		}
		//@formatter:on
	}
}
