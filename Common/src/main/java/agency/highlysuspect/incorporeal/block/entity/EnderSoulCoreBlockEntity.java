package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.corporea.FrameReader;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.ICorporeaNode;
import vazkii.botania.api.corporea.ICorporeaNodeDetector;
import vazkii.botania.api.corporea.ICorporeaRequest;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.api.state.enums.CratePattern;
import vazkii.botania.common.impl.corporea.AbstractCorporeaNode;
import vazkii.botania.common.item.ItemCraftPattern;

import java.util.Collections;
import java.util.List;

/**
 * The Ender Soul Core block entity. Provides block-level access to the owner's ender chest container.
 */
public class EnderSoulCoreBlockEntity extends AbstractSoulCoreBlockEntity {
	public EnderSoulCoreBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.ENDER_SOUL_CORE, pos, state);
	}
	
	private @Nullable PlayerEnderChestContainer enderChest = null;
	
	private int mask;
	
	@Override
	protected int getMaxMana() {
		return 5000;
	}
	
	@Override
	protected void tick() {
		enderChest = findPlayer().map(ServerPlayer::getEnderChestInventory).orElse(null);
		if(enderChest == null) {
			mask = 0;
		} else {
			mask = readMask();
		}
	}
	
	public boolean isCompletelyUnmasked() {
		return mask == 0xFFFFFFFF;
	}
	
	public boolean canAccessSlot(int slot) {
		return ((1 << slot) & mask) != 0;
	}
	
	//crate pattern indexing:
	// 00 01 02
	// 03 04 05
	// 06 07 08
	//
	//ender chest indexing:
	// 00 01 02 | 03 04 05 | 06 07 08
	// 09 10 11 | 12 13 14 | 15 16 17
	// 18 19 20 | 21 22 23 | 24 25 26
	//
	//each closed slot in the crafting pattern should mask off 3 slots in the ender chest, corresponding to their position
	//for example, slot 3 in the crate should mask off slots 9, 12, and 15 in the ender chest
	protected static int[] slotMasks = new int[]{
		~((1      ) | (1 <<  3) | (1 <<  6)), ~((1 <<  1) | (1 <<  4) | (1 <<  7)), ~((1 <<  2) | (1 <<  5) | (1 <<  8)),
		~((1 <<  9) | (1 << 12) | (1 << 15)), ~((1 << 10) | (1 << 13) | (1 << 16)), ~((1 << 11) | (1 << 14) | (1 << 17)),
		~((1 << 18) | (1 << 21) | (1 << 24)), ~((1 << 19) | (1 << 22) | (1 << 25)), ~((1 << 20) | (1 << 23) | (1 << 26))
	};
	
	protected int readMask() {
		int mask = 0xFFFFFFFF; //no bits set to 0
		
		for(ItemFrame frame : FrameReader.nonEmptyRestingOn(level, getBlockPos())) {
			if(frame.getItem().getItem() instanceof ItemCraftPattern patternItem) {
				CratePattern pattern = patternItem.pattern;
				for(int i = 0; i < 9; i++) {
					//Mask off closed slots only
					if(!pattern.openSlots.get(i)) mask &= slotMasks[i];
				}
			}
		}
		
		return mask;
	}
	
	/**
	 * Drain mana according to how many items were removed.
	 */
	public void trackItemMovement(int itemCount) {
		drainMana(5 * itemCount);
	}
	
	/**
	 * Updated once per tick.
	 */
	public @Nullable PlayerEnderChestContainer getEnderChest() {
		return enderChest;
	}
	
	public static class NodeDetector implements ICorporeaNodeDetector {
		@Nullable
		@Override
		public ICorporeaNode getNode(Level world, ICorporeaSpark spark) {
			EnderSoulCoreBlockEntity be = IncBlockEntityTypes.ENDER_SOUL_CORE.getBlockEntity(world, spark.getAttachPos());
			if(be == null) return null;
			else return new Node(be, world, spark.getAttachPos(), spark);
		}
		
		public static class Node extends AbstractCorporeaNode {
			public Node(EnderSoulCoreBlockEntity be, Level world, BlockPos pos, ICorporeaSpark spark) {
				super(world, pos, spark);
				this.be = be;
			}
			
			private final EnderSoulCoreBlockEntity be;
			
			@Override
			public List<ItemStack> countItems(ICorporeaRequest request) {
				PlayerEnderChestContainer container = be.getEnderChest();
				if(container == null) return Collections.emptyList();
				else return iterateOverSlots(container, request, false);
			}
			
			@Override
			public List<ItemStack> extractItems(ICorporeaRequest request) {
				PlayerEnderChestContainer container = be.getEnderChest();
				if(container == null) return Collections.emptyList();
				
				List<ItemStack> extracted = iterateOverSlots(container, request, true);
				for(ItemStack stack : extracted) be.trackItemMovement(stack.getCount()); //pay for the items
				return extracted;
			}
			
			//BotaniaCopy: VanillaCorporeaNode
			protected List<ItemStack> iterateOverSlots(Container inv, ICorporeaRequest request, boolean doit) {
				ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
				
				for (int i = inv.getContainerSize() - 1; i >= 0; i--) {
					//incorporeal modification: honor the mask
					if(!be.canAccessSlot(i)) continue;
					
					ItemStack stackAt = inv.getItem(i);
					if (request.getMatcher().test(stackAt)) {
						request.trackFound(stackAt.getCount());
						
						int rem = Math.min(stackAt.getCount(), request.getStillNeeded() == -1 ? stackAt.getCount() : request.getStillNeeded());
						if (rem > 0) {
							request.trackSatisfied(rem);
							
							if (doit) {
								ItemStack copy = stackAt.copy();
								copy.setCount(rem);
								if (getSpark().isCreative()) {
									builder.add(copy);
								} else {
									builder.addAll(breakDownBigStack(inv.removeItem(i, rem)));
								}
								getSpark().onItemExtracted(copy);
								request.trackExtracted(rem);
							} else {
								ItemStack copy = stackAt.copy();
								copy.setCount(rem);
								builder.add(copy);
							}
						}
					}
				}
				
				return builder.build();
			}
		}
	}
}
