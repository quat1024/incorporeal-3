package agency.highlysuspect.incorporeal.mixin;

import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemFrame.class)
public interface ItemFrameAccessor {
	/**
	 * This method is called (in vanilla) whenever an item is removed from an item frame, including
	 * cases like destroying the whole item frame or punching the item out. Its purpose is to remove
	 * the green "you are here" marker from a map, if a map was displayed in the item frame.
	 * 
	 * There isn't a general-purpose "remove and return the stack from the item frame" method
	 * in ItemFrame, so the Frame Tinkerer calls this method to ensure the map marker is updated
	 * correctly, and you don't get leftover map markers from tinkering maps out of frames.
	 * 
	 * Hey, fun fact, I didn't do this in 1.12 or 1.16. Oops.
	 */
	@Invoker("removeFramedMap") void inc$removeFramedMap(ItemStack stack);
}
