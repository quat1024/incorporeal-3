package agency.highlysuspect.incorporeal.computer.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface PositionTweakable {
	default Vec3 tweakPosition(Level level, BlockPos pos) {
		return Vec3.atCenterOf(pos);
	}
}
