package agency.highlysuspect.incorporeal.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla Minecraft only persists the rotation setting of item frames if they are not empty.
 * This can create weird behavior, where if you take an item out of a frame and then put it back in
 * (which is something you can do automatically with the Frame Tinkerer), the rotation will reset
 * if the chunk was unloaded between those two events.
 * 
 * Some of Botania's mechanics are very sensitive to item frame rotations, and the Data Funnel
 * allows you to automatically turn item frames, so fixing this is probably a good idea.
 * 
 * The mixin just does the exact same thing vanilla does, but hoisted out of the if statement
 * that only runs them when the itemstack is not empty.
 */
@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin {
	@Shadow public abstract int getRotation();
	@Shadow protected abstract void setRotation(int rotation, boolean updateComparators);
	
	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	private void whenAddingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		tag.putByte("ItemRotation", (byte) getRotation());
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	private void whenReadingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		setRotation(tag.getByte("ItemRotation"), false);
	}
}
