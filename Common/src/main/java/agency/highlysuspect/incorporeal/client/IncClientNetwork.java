package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.net.DataFunnelEffect;
import agency.highlysuspect.incorporeal.net.FunnyEffect;
import agency.highlysuspect.incorporeal.net.IncNetwork;
import agency.highlysuspect.incorporeal.net.SanvocaliaEffect;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.item.ItemTwigWand;
import vazkii.botania.common.proxy.IProxy;

import java.util.function.BiConsumer;

/**
 * The receiving end of server-to-client communication.
 * 
 * Raw FriendlyByteBufs are used, because each loader has its own network abstraction, and I just do not want to deal with it.
 */
public class IncClientNetwork {
	//a flat array would probably be better lol.
	public static Byte2ObjectMap<BiConsumer<Minecraft, FriendlyByteBuf>> handlers = new Byte2ObjectOpenHashMap<>();
	
	static {
		handlers.put(IncNetwork.Ids.FUNNY, (mc, buf) -> {
			FunnyEffect effect = FunnyEffect.unpack(buf);
			
			mc.doRunTask(() -> {
				Level level = mc.level;
				if(level == null) return;
				
				BlockPos srcPos = effect.src();
				double sparkleHeight = effect.sparkleHeight();
				Vec3 src = level.getBlockState(srcPos).getOffset(level, srcPos).add(srcPos.getX() + .5, srcPos.getY() + sparkleHeight, srcPos.getZ() + .5);
				
				for(FunnyEffect.Line line : effect.lines()) {
					Vec3 dst = Vec3.atCenterOf(line.dst());
					ItemTwigWand.doParticleBeam(level, src, dst);
					
					byte[] notes = line.notes();
					if(notes.length == 1) {
						level.addParticle(ParticleTypes.NOTE, dst.x, dst.y + 0.7, dst.z, notes[0] / 24d, 0, 0);
					} else if(notes.length == 2) {
						level.addParticle(ParticleTypes.NOTE, dst.x - 0.2, dst.y + 0.7, dst.z, notes[0] / 24d, 0, 0);
						level.addParticle(ParticleTypes.NOTE, dst.x + 0.2, dst.y + 0.7, dst.z, notes[1] / 24d, 0, 0);
					}
				}
			});
		});
		
		handlers.put(IncNetwork.Ids.SANVOCALIA, (mc, buf) -> {
			SanvocaliaEffect effect = SanvocaliaEffect.unpack(buf);
			
			mc.doRunTask(() -> {
				Level level = mc.level;
				if(level == null) return;
				for(BlockPos dst : effect.ends()) ItemTwigWand.doParticleBeam(level, effect.start(), Vec3.atCenterOf(dst));
			});
		});
		
		handlers.put(IncNetwork.Ids.DATA_FUNNEL, (mc, buf) -> {
			DataFunnelEffect effect = DataFunnelEffect.unpack(buf);
			
			mc.doRunTask(() -> {
				Level level = mc.level;
				if(level == null) return;
				for(DataFunnelEffect.Line line : effect.lines()) {
					doParticleBeamSingleColor(level, line.start(), line.end(), line.color(), 0.2, 2f);
				}
			});
		});
	}
	
	//Kinda copypasterino from ItemTwigWand with funny modifications
	//Itemtwigwand's "spacing" is 0.05 and "size" is 0.5, for reference
	public static void doParticleBeamSingleColor(Level world, Vec3 orig, Vec3 end, int color, double spacing, float size) {
		float r = (color >> 16 & 0xFF) / 255F;
		float g = (color >> 8 & 0xFF) / 255F;
		float b = (color & 0xFF) / 255F;
		
		Vec3 diff = end.subtract(orig);
		Vec3 movement = diff.normalize().scale(spacing);
		int iters = (int) (diff.length() / movement.length());
		
		Vec3 currentPos = orig;
		for (int i = 0; i < iters; i++) {
			SparkleParticleData data = SparkleParticleData.noClip(size, r, g, b, 4);
			IProxy.INSTANCE.addParticleForceNear(world, data, currentPos.x, currentPos.y, currentPos.z, 0, 0, 0);
			currentPos = currentPos.add(movement);
		}
	}
	
	public static void initialize() {
		//Classload
	}
	
	//called on the network thread - Watch out!!
	public static void handle(FriendlyByteBuf buf) {
		//Read the first byte to determine the packet type
		byte which = buf.readByte();
		//then dispatch the rest of the buffer to the packet handler
		BiConsumer<Minecraft, FriendlyByteBuf> handler = handlers.get(which);
		if(handler != null) handler.accept(Minecraft.getInstance(), buf);
	}
}
