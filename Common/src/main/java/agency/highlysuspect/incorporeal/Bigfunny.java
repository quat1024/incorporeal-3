package agency.highlysuspect.incorporeal;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.jetbrains.annotations.Nullable;

public class Bigfunny {
	private static final byte[] HUZZAH = new byte[] {-126, 34, 114, 34, 82, 2, 0, 0, 5, 85, 82, 53, 33, 34, 17, 17, 21, 85, 82, 120, 35, 34, 51, 51, 56, -120, -126, -86, 39, 34, 34, 34, -126, 34, 114, 34, 82, 2, 0, 0, 5, 85, 82, 53, 33, 34, 17, 17, 21, 85, 82, 120, 35, 34, 51, 51, 56, -120, -126, -86, 39, 34, 34, 34, 34, -89, -84, 34, -54, -54, -62, -84, 42, -54, -62, -35, 40, 34, 34, 40, 45, -35, 45, -3, 44, 34, 34, 34, 44, -52, 47, -36, 42, 34, -86, -86, -86, -86, 114, -126, -54, -54, -54, -62, 42, -54, -62, -35, 40, 34, 34, 40, 45, -35, -46, -3, 44, 34, 34, 34, 44, -52, 47, -36, 42, 34, 34, 34, 4, 4, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 0, 0, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 0, 0, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 8, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, -91, -1, -1, -1, -81, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, -1, -1, -1, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, -1, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, -91, 111, 105, -1, 105, -1, -91, -1, -91, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, -91, -1, 105, -1, 105, -1, 50, 34, 34, 34, 82, 34, -126, -62, 82, 34, -126, -62, 18, 34, 82, -126, 18, 34, 82, -126, -126, 34, -62, -14, -126, 34, -62, -14, 50, 34, 114, -94};
	
	@Nullable
	public static byte[] notesForTick(int tick, NoteBlockInstrument inst) {
		int t = tick % 256;
		
		if(inst == NoteBlockInstrument.FLUTE) {
			byte unpacked = biunpack(0, t);
			return unpacked == 2 ? null : one(unpacked);
		}
		
		if(inst == NoteBlockInstrument.SNARE) {
			boolean low = unpackSnare(t * 2);
			boolean high = unpackSnare(t * 2 + 1);
			if(!low && !high) return null;
			else if(low && !high) return one((byte) 8);
			else if(!low) return one((byte) 22);
			else return two((byte) 8, (byte) 22);
		}
		
		if(inst == NoteBlockInstrument.BASEDRUM) {
			if(t % 2 == 1) return null;
			
			byte packed = HUZZAH[192 + t / 2];
			if(packed == -1) return null;
			
			byte a = (byte) ((packed & 0xF0) >> 4);
			if((packed & 0xF) == 0xF) {
				return one(a);
			}
			byte b = (byte) ((packed & 0xF) - 1);
			return two(a, b);
		}
		
		if(inst == NoteBlockInstrument.BASS) {
			if(t == 0 || t == 128) return null;
			
			byte unpacked = biunpack(320, t % 64);
			return unpacked == 2 ? null : one(unpacked);
		}
		
		return null;
	}
	
	private static byte[] one(byte x) {
		return new byte[] { x };
	}
	
	private static byte[] two(byte x, byte y) {
		return new byte[] { x, y };
	}
	
	private static byte biunpack(int wholeOffset, int index) {
		boolean even = index % 2 == 0;
		return (byte) ((HUZZAH[wholeOffset + index / 2] & (even ? 0b11110000 : 0b00001111)) >>> (even ? 4 : 0));
	}
	
	private static boolean unpackSnare(int index_) {
		int index = index_ / 8;
		int shift = 7 - (index_ % 8);
		int mask = 0b00000001 << shift;
		return ((HUZZAH[128 + index] & mask) >>> shift) == 1;
	}
}
