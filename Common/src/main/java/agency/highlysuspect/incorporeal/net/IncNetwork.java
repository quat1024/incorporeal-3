package agency.highlysuspect.incorporeal.net;

import agency.highlysuspect.incorporeal.platform.IncXplat;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Because networking apis are very different across loaders, this is an attempt to
 * write a convenient abstraction on top of both. Both loaders only register 1 packet that
 * all networking goes through. It is not a full abstraction, only covering the things I need
 * at the moment (simple s -> c networking).
 * 
 * Raw FriendlyByteBufs are used and data is manually packed into them. 
 */
public class IncNetwork {
	public static void sendTo(Packable data, ServerPlayer player) {
		IncXplat.INSTANCE.sendTo(pack(data), player);
	}
	
	public static void sendToAllWatching(Packable data, ServerLevel level, BlockPos pos) {
		//TODO: avoid calling pack() over and over, maybe
		for(ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false)) {
			sendTo(data, player);
		}
	}
	
	public static FriendlyByteBuf pack(Packable data) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeByte(data.packId());
		data.pack(buf);
		return buf;
	}
	
	interface Packable {
		byte packId();
		void pack(FriendlyByteBuf buf);
		
		//conveniences
		default void sendTo(ServerPlayer player) {
			IncNetwork.sendTo(this, player);
		}
		
		default void sendToAllWatching(ServerLevel level, BlockPos pos) {
			IncNetwork.sendToAllWatching(this, level, pos);
		}
	}
	
	//helpers. Forgive the mess.
	
	static void writeVec3(FriendlyByteBuf buf, Vec3 vec) {
		buf.writeDouble(vec.x);
		buf.writeDouble(vec.y);
		buf.writeDouble(vec.z);
	}
	
	static Vec3 readVec3(FriendlyByteBuf buf) {
		return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
	static <T> void writeCollection(FriendlyByteBuf buf, Collection<T> things, BiConsumer<T, FriendlyByteBuf> packer) {
		if(things.size() > Byte.MAX_VALUE) throw new IllegalStateException("too many things!");
		buf.writeByte(things.size());
		for(T thing : things) {
			packer.accept(thing, buf);
		}
	}
	
	static <T, C extends Collection<T>> C readCollection(FriendlyByteBuf buf, Supplier<C> collectionMaker, Function<FriendlyByteBuf, T> unpacker) {
		byte howMany = buf.readByte();
		C c = collectionMaker.get();
		for(byte i = 0; i < howMany; i++) {
			c.add(unpacker.apply(buf));
		}
		return c;
	}
	
	public static class Ids {
		public static final byte FUNNY = 0;
		public static final byte SANVOCALIA = 1;
	}
}
