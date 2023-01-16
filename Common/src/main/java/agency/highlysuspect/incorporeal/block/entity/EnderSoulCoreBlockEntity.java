package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.CorporeaNode;
import vazkii.botania.api.corporea.CorporeaNodeDetector;
import vazkii.botania.api.corporea.CorporeaRequest;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.common.impl.corporea.AbstractCorporeaNode;

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
	
	@Override
	protected int getMaxMana() {
		return 5000;
	}
	
	@Override
	protected void tick() {
		if(level == null || level.isClientSide) return;
		enderChest = findPlayer().map(Player::getEnderChestInventory).orElse(null);
	}
	
	@Override
	public int computeSignal() {
		if(enderChest == null) return 0;
		else return AbstractContainerMenu.getRedstoneSignalFromContainer(enderChest);
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
	
	public static class NodeDetector implements CorporeaNodeDetector {
		@Nullable
		@Override
		public CorporeaNode getNode(Level world, CorporeaSpark spark) {
			EnderSoulCoreBlockEntity be = IncBlockEntityTypes.ENDER_SOUL_CORE.getBlockEntity(world, spark.getAttachPos());
			if(be == null) return null;
			else return new Node(be, world, spark.getAttachPos(), spark);
		}
		
		public static class Node extends AbstractCorporeaNode {
			public Node(EnderSoulCoreBlockEntity be, Level world, BlockPos pos, CorporeaSpark spark) {
				super(world, pos, spark);
				this.be = be;
			}
			
			private final EnderSoulCoreBlockEntity be;
			
			@Override
			public List<ItemStack> countItems(CorporeaRequest request) {
				PlayerEnderChestContainer container = be.getEnderChest();
				if(container == null) return Collections.emptyList();
				else return iterateOverSlots(container, request, false);
			}
			
			@Override
			public List<ItemStack> extractItems(CorporeaRequest request) {
				PlayerEnderChestContainer container = be.getEnderChest();
				if(container == null) return Collections.emptyList();
				
				List<ItemStack> extracted = iterateOverSlots(container, request, true);
				for(ItemStack stack : extracted) be.trackItemMovement(stack.getCount()); //pay for the items
				return extracted;
			}
			
			//BotaniaCopy: VanillaCorporeaNode
			protected List<ItemStack> iterateOverSlots(Container inv, CorporeaRequest request, boolean doit) {
				ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
				
				for (int i = inv.getContainerSize() - 1; i >= 0; i--) {
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
