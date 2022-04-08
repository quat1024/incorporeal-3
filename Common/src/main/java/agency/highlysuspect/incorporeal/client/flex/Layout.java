package agency.highlysuspect.incorporeal.client.flex;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;

public class Layout {
	public Map<Element, Result> layoutResults = new IdentityHashMap<>();
	
	public Result get(Element e) {
		return layoutResults.computeIfAbsent(e, __ -> new Result());
	}
	
	public void setSize(Element e, int width, int height) {
		get(e).setSize(width, height);
	}
	
	public void dump() {
		//i deserve to see a nice table!!
		int widestChungus = layoutResults.keySet().stream().max(Comparator.comparingInt(e -> e.name.length())).get().name.length();
		
		layoutResults.forEach((element, result) -> {
			String pad = " ".repeat(widestChungus - element.name.length());
			System.out.println(element.name + pad + " -> " + result);
		});
	}
	
	//Not a record (mutable)
	public static final class Result {
		public Result(int relativeX, int relativeY, int width, int height) {
			this.relativeX = relativeX;
			this.relativeY = relativeY;
			this.width = width;
			this.height = height;
		}
		
		public Result() {
			this(0, 0, 0, 0);
		}
		
		public int relativeX;
		public int relativeY;
		public int width;
		public int height;
		
		public void setSize(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public void setRelativePositionAlong(FlexElement.Direction dir, int pos) {
			switch(dir) {
				case ROW -> relativeX = pos;
				case COLUMN -> relativeY = pos;
			}
		}
		
		public int sizeAlong(FlexElement.Direction dir) {
			return switch(dir) {
				case ROW -> width;
				case COLUMN -> height;
			};
		}
		
		@Override
		public String toString() {
			return "x: %s, y: %s, width: %s, height: %s".formatted(relativeX, relativeY, width, height);
		}
	}
}
