package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.client.IncClientNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class IncForgeNetworking {
	public static final String PROTOCOL_VERSION = "Potato";
	public static final SimpleChannel EXTREMELY_SIMPLE_CHANNEL_CANT_YOU_SEE_HOW_SIMPLE_IT_IS = NetworkRegistry.ChannelBuilder.named(Inc.id("awesome_simple_channel")).networkProtocolVersion(() -> PROTOCOL_VERSION).clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals).simpleChannel();
	
	public static void init() {
		EXTREMELY_SIMPLE_CHANNEL_CANT_YOU_SEE_HOW_SIMPLE_IT_IS.registerMessage(0, FriendlyByteBuf.class, IncForgeNetworking::encode, IncForgeNetworking::decode, ClassloadingParanoia::handle);
	}
	
	//literally just pouring my bytebuf into theirs
	//with this Super Simple!!! channel, i cant be trusted to construct network packets myself
	public static void encode(FriendlyByteBuf mine, FriendlyByteBuf theirs) {
		theirs.writeInt(mine.readableBytes());
		theirs.writeBytes(mine);
		mine.release();
	}
	
	public static FriendlyByteBuf decode(FriendlyByteBuf theirs) {
		int len = theirs.readInt();
		FriendlyByteBuf mine = new FriendlyByteBuf(Unpooled.buffer());
		theirs.readBytes(mine, len);
		return mine;
	}
	
	//I dont fucking know why forge geniuses decided to classload this end on the server but im paranoid
	public static class ClassloadingParanoia {
		public static void handle(FriendlyByteBuf buf, Supplier<NetworkEvent.Context> shit) {
			NetworkEvent.Context ctx = shit.get();
			ctx.setPacketHandled(true); //when the
			
			//Sure Idk have another layer of proxies I dont even care anymore
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> IncClientNetwork.handle(buf));
			
			buf.release(); //i guess?
		}
	}
}
