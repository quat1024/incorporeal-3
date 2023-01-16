package agency.highlysuspect.incorporeal.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.block.WandBindable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.helper.MathHelper;

import java.util.Objects;
import java.util.Set;

/**
 * A BlockEntity that can be bound to a whole bunch of different targets, instead of just one.
 * Botania doesn't have this concept, so I need to do some hackery to make it render properly.
 * 
 * @see agency.highlysuspect.incorporeal.mixin.client.ItemTwigWandMixin
 */
public interface Multibindable extends WandBindable {
	Set<BlockPos> getBindings(); //Make it mutable so the default implementations below work
	
	//Factoring out some common code...
	
	default int getBindRange() {
		return 0; //override pls
	}
	
	//IWandBindable default implementation
	
	@Override
	default boolean canSelect(Player player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	default boolean bindTo(Player player, ItemStack wand, BlockPos target, Direction side) {
		if(!(this instanceof BlockEntity be)) throw new IllegalArgumentException("what");
		
		//binding to the same target again will unbind
		if(getBindings().contains(target)) { //linear scan smh
			getBindings().remove(target); //linear scan smh
			be.setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(be);
			return true;
		}
		
		if(getBindings().size() >= 10) return false; //gotta stop somewhere
		if(Objects.equals(be.getBlockPos(), target)) return false; //don't allow self-binds?
		if(MathHelper.distSqr(be.getBlockPos(), target) > (long) getBindRange() * getBindRange()) return false; //limit range
		
		getBindings().add(target);
		be.setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(be);
		return true;
	}
	
	@Nullable
	@Override
	default BlockPos getBinding() {
		//This method is required by IWandBindable; getBinding is typically used to render the overlay.
		//Thing is, I can only return one BlockPos from this method, but I have possibly lots of bindings to render.
		//So: bindings are rendered in a totally different way (see ItemTwigWandMixin) and the default rendering is suppressed.
		return null;
	}
}
