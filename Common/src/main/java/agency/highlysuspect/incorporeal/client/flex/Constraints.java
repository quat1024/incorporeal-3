package agency.highlysuspect.incorporeal.client.flex;

import com.google.common.base.Preconditions;
import net.minecraft.util.Mth;

public record Constraints(int minWidth, int maxWidth, int minHeight, int maxHeight) {
	public Constraints {
		Preconditions.checkArgument(minWidth != INFINITY, "Infinite minimum width");
		Preconditions.checkArgument(minHeight != INFINITY, "Infinite minimum height");
		Preconditions.checkArgument(minWidth <= maxWidth, "minWidth > maxWidth");
		Preconditions.checkArgument(minHeight <= maxHeight, "minHeight > maxHeight");
	}
	
	public static int INFINITY = 99999999; //close enough right?
	
	public static Constraints tight(int width, int height) {
		return new Constraints(width, width, height, height);
	}
	
	public static Constraints infinite() {
		return new Constraints(0, INFINITY, 0, INFINITY);
	}
	
	public int clampWidth(int width) {
		return Mth.clamp(width, minWidth, maxWidth);
	}
	
	public int clampHeight(int height) {
		return Mth.clamp(height, minHeight, maxHeight);
	}
	
	public Constraints withWidthBounds(int newMinWidth, int newMaxWidth) {
		return new Constraints(newMinWidth, newMaxWidth, minHeight, maxHeight);
	}
	
	public Constraints withTightWidth(int newWidth) {
		return withWidthBounds(newWidth, newWidth);
	}
	
	public Constraints withInfiniteWidth() {
		return withWidthBounds(0, INFINITY);
	}
	
	public Constraints withHeightBounds(int newMinHeight, int newMaxHeight) {
		return new Constraints(minWidth, maxWidth, newMinHeight, newMaxHeight);
	}
	
	public Constraints withTightHeight(int newHeight) {
		return withHeightBounds(newHeight, newHeight);
	}
	
	public Constraints withInfiniteHeight() {
		return withHeightBounds(0, INFINITY);
	}
	
	public Constraints withBoundsAlong(FlexElement.Direction dir, int newMin, int newMax) {
		return switch(dir) {
			case ROW -> withWidthBounds(newMin, newMax);
			case COLUMN -> withHeightBounds(newMin, newMax);
		};
	}
	
	public Constraints withTightBoundsAlong(FlexElement.Direction dir, int newSize) {
		return switch(dir) {
			case ROW -> withTightWidth(newSize);
			case COLUMN -> withTightHeight(newSize);
		};
	}
	
	public Constraints withInfiniteAlong(FlexElement.Direction dir) {
		return switch(dir) {
			case ROW -> withInfiniteWidth();
			case COLUMN -> withInfiniteHeight();
		};
	}
	
	public int maxAlong(FlexElement.Direction dir) {
		return switch(dir) {
			case ROW -> maxWidth;
			case COLUMN -> maxHeight;
		};
	}
	
	public int minAlong(FlexElement.Direction dir) {
		return switch(dir) {
			case ROW -> minWidth;
			case COLUMN -> minHeight;
		};
	}
	
	public boolean isInfiniteWidth() {
		return maxWidth == INFINITY;
	}
	
	public boolean isInfiniteHeight() {
		return maxHeight == INFINITY;
	}
	
	public boolean isInfiniteAlong(FlexElement.Direction dir) {
		return switch(dir) {
			case ROW -> isInfiniteWidth();
			case COLUMN -> isInfiniteHeight();
		};
	}
}
