package agency.highlysuspect.incorporeal.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

/**
 * Packet that the Data Funnel uses to show particle effects.
 */
public record DataFunnelEffect(ArrayList<Line> lines) implements IncNetwork.Packable {
	public DataFunnelEffect() {
		this(new ArrayList<>());
	}
	
	public static record Line(Vec3 start, Vec3 end, int color) {
		public void pack(FriendlyByteBuf buf) {
			IncNetwork.writeVec3(buf, start);
			IncNetwork.writeVec3(buf, end);
			buf.writeInt(color);
		}
		
		public static Line unpack(FriendlyByteBuf buf) {
			return new Line(
				IncNetwork.readVec3(buf),
				IncNetwork.readVec3(buf),
				buf.readInt()
			);
		}
	}
	
	//mutable record YAY!!!!!
	public void addLine(Vec3 start, Vec3 end, int color) {
		//see IncNetwork#writeCollection
		//this time it's a silent-drop and not an error, because it might be possible to actually reach this limit lmao
		if(lines.size() == Byte.MAX_VALUE) return;
		lines.add(new Line(start, end, color));
	}
	
	public boolean isEmpty() {
		return lines.isEmpty();
	}
	
	@Override
	public byte packId() {
		return IncNetwork.Ids.DATA_FUNNEL;
	}
	
	@Override
	public void pack(FriendlyByteBuf buf) {
		IncNetwork.writeCollection(buf, lines, Line::pack);
	}
	
	public static DataFunnelEffect unpack(FriendlyByteBuf buf) {
		return new DataFunnelEffect(IncNetwork.readCollection(buf, ArrayList::new, Line::unpack));
	}
}
