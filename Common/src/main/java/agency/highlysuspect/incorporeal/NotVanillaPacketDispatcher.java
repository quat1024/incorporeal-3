package agency.highlysuspect.incorporeal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class NotVanillaPacketDispatcher {
	public static void dispatchToNearbyPlayersExcept(BlockEntity be, @Nullable Player skip) {
		if(be.getLevel() instanceof ServerLevel serverLevel) {
			Packet<?> packet = be.getUpdatePacket();
			if(packet == null) return;
			
			ChunkPos pos = new ChunkPos(be.getBlockPos());
			for(ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(pos, false)) {
				if(player != skip) player.connection.send(packet);
			}
		}
	}
}
