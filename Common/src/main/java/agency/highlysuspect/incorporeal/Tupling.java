package agency.highlysuspect.incorporeal;

import net.minecraft.util.Mth;

import java.util.Locale;

public enum Tupling {
	SINGLE, DOUBLE, TRIPLE, QUADRUPLE, QUINTUPLE, SEXTUPLE, SEPTUPLE, OCTUPLE;
	
	public String compressedPrefix(String end) {
		return name().toLowerCase(Locale.ROOT) + "_compressed_" + end;
	}
	
	public int level() {
		return ordinal() + 1;
	}
	
	public int count(int root) {
		return (int) Math.pow(root, level());
	}
	
	public float getTaterRadius() {
		return Inc.rangeRemap(level(), 0, 8, 2/16f, 0.5f);
	}
	
	public float getTaterScaleFactor() {
		return Inc.rangeRemap(level(), 0, 8, 1, 4);
	}
}
