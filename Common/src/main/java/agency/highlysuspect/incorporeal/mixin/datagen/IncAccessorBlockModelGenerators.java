package agency.highlysuspect.incorporeal.mixin.datagen;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * So named because Botania also has one.
 */
@Mixin(BlockModelGenerators.class)
public interface IncAccessorBlockModelGenerators {
	/**
	 * Used by repeaters & comparators, its the same as createHorizontalDispatch, but rotated by 180 degrees.
	 * Thanks mojang
	 */
	@Invoker("createHorizontalFacingDispatchAlt")
	static PropertyDispatch horizontalDispatchAlt() {
		throw new IllegalStateException();
	}
}
