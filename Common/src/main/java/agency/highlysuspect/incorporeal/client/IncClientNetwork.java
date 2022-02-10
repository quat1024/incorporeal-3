package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.net.FunnyEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class IncClientNetwork {
	public static Map<String, BiConsumer<Minecraft, FriendlyByteBuf>> handlers = new HashMap<>();
	
	static {
		handlers.put("funny", (mc, buf) -> {
			FunnyEffect effect = FunnyEffect.unpack(buf);
			
			mc.doRunTask(() -> {
				System.out.println(effect); //TODO
			});
		});
	}
	
	public static void initialize() {
		//Classload
	}
	
	//called on the network thread - Watch out
	public static void handle(Minecraft client, FriendlyByteBuf buf) {
		String which = buf.readUtf();
		BiConsumer<Minecraft, FriendlyByteBuf> handler = handlers.get(which);
		if(handler != null) {
			//give it the rest of the buf
			handler.accept(client, buf);
		}
	}
}
