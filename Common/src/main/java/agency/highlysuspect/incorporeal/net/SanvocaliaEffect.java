package agency.highlysuspect.incorporeal.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public record SanvocaliaEffect(Vec3 start, Set<BlockPos> ends) implements IncNetwork.Packable {
	@Override
	public byte packId() {
		return IncNetwork.Ids.SANVOCALIA;
	}
	
	@Override
	public void pack(FriendlyByteBuf buf) {
		IncNetwork.writeVec3(buf, start);
		IncNetwork.writeCollection(buf, ends, (pos, buff) -> buff.writeBlockPos(pos));
	}
	
	public static SanvocaliaEffect unpack(FriendlyByteBuf buf) {
		Vec3 start = IncNetwork.readVec3(buf);
		Set<BlockPos> ends = IncNetwork.readCollection(buf, HashSet::new, FriendlyByteBuf::readBlockPos);
		return new SanvocaliaEffect(start, ends);
	}
}
