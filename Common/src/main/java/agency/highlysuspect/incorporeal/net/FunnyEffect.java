package agency.highlysuspect.incorporeal.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public record FunnyEffect(List<Line> lines) implements IncNetwork.Packable {
	public FunnyEffect() {
		this(new ArrayList<>());
	}
	
	public static record Line(Vec3 start, Vec3 end, byte[] notes) {
		void pack(FriendlyByteBuf buf) {
			IncNetwork.writeVec3(buf, start);
			IncNetwork.writeVec3(buf, end);
			buf.writeByteArray(notes);
		}
		
		static Line unpack(FriendlyByteBuf buf) {
			return new Line(
				IncNetwork.readVec3(buf),
				IncNetwork.readVec3(buf),
				buf.readByteArray(5)
			);
		}
	}
	
	//mutable record :yeefyeef:
	public void addLine(Vec3 start, Vec3 end, byte[] notes) {
		if(lines.size() > Byte.MAX_VALUE) throw new IllegalStateException("too many lines!");
		if(notes.length > 5) throw new IllegalStateException("too many notes!");
		lines.add(new Line(start, end, notes));
	}
	
	public boolean isEmpty() {
		return lines.isEmpty();
	}
	
	@Override
	public String packId() {
		return "funny";
	}
	
	@Override
	public void pack(FriendlyByteBuf buf) {
		IncNetwork.writeList(buf, lines, Line::pack);
	}
	
	public static FunnyEffect unpack(FriendlyByteBuf buf) {
		return new FunnyEffect(IncNetwork.readList(buf, Line::unpack));
	}
}
