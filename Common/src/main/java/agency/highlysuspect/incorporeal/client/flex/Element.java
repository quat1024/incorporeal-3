package agency.highlysuspect.incorporeal.client.flex;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
	public Element(String name) {
		this.name = name;
	}
	
	//for debugging mainly lol
	public final String name;
	
	//children
	public List<Element> children = new ArrayList<>();
	
	//properties relevant to being stuck into a FlexElement
	public int flexGrow;
	public @Nullable FlexElement.Alignment alignSelf = null;
	
	/**
	 * Walk the tree of child elements.
	 * On the way up, Layout needs to be told about:
	 * - each child's position relative to myself,
	 * - my own width and height;
	 * 
	 * Elements do not directly set the width and height of their own children, but that behavior
	 * can be requested by refining BoxConstraints on the way down the tree traversal.
	 * 
	 * Elements never know their absolute position, and do not set their own relative position.
	 */
	public abstract void solve(Constraints constraints, Layout layout);
	
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
		
		public boolean isInfiniteAlong(FlexElement.Direction dir) {
			return switch(dir) {
				case ROW -> maxWidth == INFINITY;
				case COLUMN -> maxHeight == INFINITY;
			};
		}
	}
	
	public record Size(int width, int height) {}
}
