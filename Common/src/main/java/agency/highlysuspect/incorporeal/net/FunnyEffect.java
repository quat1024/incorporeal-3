package agency.highlysuspect.incorporeal.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record FunnyEffect(BlockPos src, double sparkleHeight, List<Line> lines) implements IncNetwork.Packable {
	public FunnyEffect(BlockPos src, double sparkleHeight) {
		this(src, sparkleHeight, new ArrayList<>());
	}
	
	public static record Line(BlockPos dst, byte[] notes) {
		void pack(FriendlyByteBuf buf) {
			buf.writeBlockPos(dst);
			buf.writeByteArray(notes);
		}
		
		static Line unpack(FriendlyByteBuf buf) {
			return new Line(
				buf.readBlockPos(),
				buf.readByteArray(5)
			);
		}
	}
	
	//mutable record :yeefyeef:
	public void addLineTo(BlockPos end, byte[] notes) {
		if(lines.size() > Byte.MAX_VALUE) throw new IllegalStateException("too many lines!");
		if(notes.length > 5) throw new IllegalStateException("too many notes!");
		lines.add(new Line(end, notes));
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
		buf.writeBlockPos(src);
		buf.writeDouble(sparkleHeight);
		IncNetwork.writeCollection(buf, lines, Line::pack);
	}
	
	public static FunnyEffect unpack(FriendlyByteBuf buf) {
		BlockPos src = buf.readBlockPos();
		double sparkleHeight = buf.readDouble();
		List<Line> lines = IncNetwork.readCollection(buf, ArrayList::new, Line::unpack);
		return new FunnyEffect(src, sparkleHeight, lines);
	}
}
