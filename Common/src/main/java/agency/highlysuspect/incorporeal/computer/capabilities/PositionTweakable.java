package agency.highlysuspect.incorporeal.computer.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Minecraft's raycast engine (BlockGetter#traverseBlocks) goes block-by-block.
 * It's possible for something like, e.g. a DatumProvider, to exist that isn't neatly on the block grid (such as an item frame).
 * This method exists to allow those capabilities to more accurately report their position.
 * This is... basically just used when drawing the sparkly glitter lines.
 */
public interface PositionTweakable {
	//I tried to pick a sensible default for the things that *are* snapped to the block grid, though.
	default Vec3 tweakPosition(Level level, BlockPos pos) {
		VoxelShape shape = level.getBlockState(pos).getShape(level, pos);
		
		//Shapes.block() returns a (private) static field, it's a very common default value.
		//so comparing with == makes sense for an early exit.
		if(shape.isEmpty() || shape == Shapes.block()) return Vec3.atCenterOf(pos);
		else return Vec3.atLowerCornerOf(pos).add(shape.bounds().getCenter());
	}
}
