package agency.highlysuspect.incorporeal.platform.fabric.block.entity;

import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.corporea.LyingCorporeaNode;
import agency.highlysuspect.incorporeal.util.ContainerUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.CorporeaNode;
import vazkii.botania.api.corporea.CorporeaRequest;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.impl.corporea.DummyCorporeaNode;

import java.util.List;

/**
 * Implementation of the Red Stringed Liar on Fabric.
 * Here I got lazy and used Container because fabric transfer api is a mess.
 * Someone contribute a fabric transfer api corporea node pls.
 */
public class FabricRedStringLiarBlockEntity extends RedStringLiarBlockEntity {
	public FabricRedStringLiarBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
	
	//PASTE FROM BOTANIA FABRIC RED STRING CONTAINER
	private BlockPos clientPos;
	
	@Override
	public void onBound(BlockPos pos) {
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		// We cannot query for the storage api on the client - so we send the binding position.
		BlockPos binding = getBinding();
		if (binding == null) {
			// hack: empty NBT gets the packet ignored but we don't want that
			cmp.putByte("-", (byte) 0);
			return;
		}
		cmp.putInt("bindX", binding.getX());
		cmp.putInt("bindY", binding.getY());
		cmp.putInt("bindZ", binding.getZ());
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		if (cmp.contains("bindX")) {
			clientPos = new BlockPos(cmp.getInt("bindX"), cmp.getInt("bindY"), cmp.getInt("bindZ"));
		} else {
			clientPos = null;
		}
	}
	
	@Nullable
	@Override
	public BlockPos getBinding() {
		assert level != null;
		return level.isClientSide ? clientPos : super.getBinding();
	}
	//END PASTE
	
	@Override
	public boolean acceptBlock(BlockPos pos) {
		assert level != null;
		return ContainerUtil.getBlockContainerAt(level, pos) != null;
	}
	
	@Override
	public @NotNull CorporeaNode createCorporeaNode(CorporeaSpark spark) {
		assert level != null;
		BlockPos binding = getBinding();
		if(binding == null) return new DummyCorporeaNode(level, spark.getAttachPos(), spark);
		
		Container cont = ContainerUtil.getBlockContainerAt(level, binding);
		if(cont == null) return new DummyCorporeaNode(level, spark.getAttachPos(), spark);
		
		else return new Node(level, spark.getAttachPos(), spark, readSpoofStacks(), cont);
	}
	
	public static class Node extends LyingCorporeaNode {
		public Node(Level world, BlockPos pos, CorporeaSpark spark, List<ItemStack> spoofStacks, Container inv) {
			super(world, pos, spark, spoofStacks);
			this.inv = inv;
		}
		
		protected final Container inv;
		
		@Override
		protected int countItemsInInventory() {
			int howMany = 0;
			for(int i = 0; i < inv.getContainerSize(); i++) {
				howMany += inv.getItem(i).getCount();
			}
			return howMany;
		}
		
		//Based on copy from VanillaCorporeaNode
		//@formatter:off
		@Override
		protected List<ItemStack> extractEverything(CorporeaRequest request) {
			ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
			
			for (int i = inv.getContainerSize() - 1; i >= 0; i--) {
				ItemStack stackAt = inv.getItem(i);
				//if (request.getMatcher().test(stackAt)) { //Incorporeal: Do not test the request matcher
					request.trackFound(stackAt.getCount());
					
					int rem = Math.min(stackAt.getCount(), request.getStillNeeded() == -1 ? stackAt.getCount() : request.getStillNeeded());
					if (rem > 0) {
						request.trackSatisfied(rem);
						
						//if (doit) { //Incorporeal: remove dry-run functionality
							ItemStack copy = stackAt.copy();
							copy.setCount(rem);
							if (getSpark().isCreative()) {
								builder.add(copy);
							} else {
								builder.addAll(breakDownBigStack(inv.removeItem(i, rem)));
							}
							getSpark().onItemExtracted(copy);
							request.trackExtracted(rem);
						//} else {
						//	ItemStack copy = stackAt.copy();
						//	copy.setCount(rem);
						//	builder.add(copy);
						//}
					}
				//}
			}
			
			return builder.build();
		}
		//@formatter:on
	}
}
