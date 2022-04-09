package agency.highlysuspect.incorporeal.client.flex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class FlexElement extends Element {
	public FlexElement(String name) {
		super(name);
	}
	
	public Direction flexDirection = Direction.ROW;
	public Alignment mainAxisAlignment = Alignment.CENTER; //justify-content
	public Alignment crossAxisAlignment = Alignment.CENTER; //align-items
	
	public List<Element> children = new ArrayList<>();
	
	@Override
	public void visitChildren(Consumer<Element> visitor) {
		children.forEach(visitor);
	}
	
	public void addChildren(Element... x) {
		Collections.addAll(children, x);
	}
	
	@Override
	public void solve(Constraints constraints) {
		int totalMainAxisSpace = constraints.maxAlong(flexDirection);
		Direction crossDirection = flexDirection.crossAxis();
		int totalCrossAxisSpace = constraints.maxAlong(crossDirection);
		
		//By convention in this system, a maximum dimension of INFINITY means "return a size as close as possible to the minimum dimension".
		//I am too lazy to implement this for flex containers. Call me back in a bit.
		boolean shrinkwrapSelf = totalMainAxisSpace == Constraints.INFINITY;
		
		Constraints shrinkwrappingAlongFlexDirectionConstraints = constraints.withInfiniteAlong(flexDirection);
		int usedMainAxisSpace = 0; //The total width of the row if all elements were rammed up into each other.
		int usedCrossAxisSpace = constraints.minAlong(crossDirection);
		
		if(shrinkwrapSelf) {
			//In one pass. Compute the layout of all children, ignoring their flex-grow.
			for(Element child : children) {
				child.solve(shrinkwrappingAlongFlexDirectionConstraints);
				
				usedMainAxisSpace += sizeAlong(child, flexDirection);
				usedCrossAxisSpace = Math.max(usedCrossAxisSpace, sizeAlong(child, crossDirection));
			}
		} else {
			//In two passes.
			//First, figure out how much space that inflexible elements take up along the main axis.
			//While we're here, also solve layouts on the inflexible elements, and compute the total flex-grow of the flexible ones.
			List<Element> flexibleChildren = new ArrayList<>();
			int totalFlexGrow = 0;
			for(Element child : children) {
				if(child.flexGrow != 0) {
					totalFlexGrow += child.flexGrow;
					flexibleChildren.add(child);
					continue;
				}
				
				child.solve(shrinkwrappingAlongFlexDirectionConstraints);
				
				usedMainAxisSpace += sizeAlong(child, flexDirection);
				usedCrossAxisSpace = Math.max(usedCrossAxisSpace, sizeAlong(child, crossDirection));
			}
			
			//Next, if there were any flexible children, distribute the remaining space to them. And solve their layout.
			if(totalFlexGrow != 0) {
				int spacePerFlexGrow = (totalMainAxisSpace - usedMainAxisSpace) / totalFlexGrow;
				for(Element child : flexibleChildren) {
					Constraints flexibleConstraints = constraints.withTightBoundsAlong(flexDirection, spacePerFlexGrow * child.flexGrow);
					child.solve(flexibleConstraints);
					
					usedMainAxisSpace += sizeAlong(child, flexDirection);
					usedCrossAxisSpace = Math.max(usedCrossAxisSpace, sizeAlong(child, crossDirection));
				}
			}
		}
		
		//B. Position each item along the main and cross axes.
		int remainingMainAxisSpace = shrinkwrapSelf ? 0 : totalMainAxisSpace - usedMainAxisSpace;
		int runningMainPos = mainAxisAlignment.florp(remainingMainAxisSpace);
		
		for(Element child : children) {
			
			setRelativePositionAlong(child, flexDirection, runningMainPos);
			runningMainPos += sizeAlong(child, flexDirection);
			
			int remainingCrossAxisSpace = totalCrossAxisSpace - sizeAlong(child, crossDirection);
			setRelativePositionAlong(child, crossDirection, (child.alignSelf != null ? child.alignSelf : crossAxisAlignment).florp(remainingCrossAxisSpace));
		}
		
		//C. Set my own size
		width = flexDirection == Direction.ROW ? usedMainAxisSpace : usedCrossAxisSpace;
		height = flexDirection == Direction.ROW ? usedCrossAxisSpace : usedMainAxisSpace;
	}
	
	private int sizeAlong(Element element, Direction dir) {
		return switch(dir) {
			case ROW -> element.width;
			case COLUMN -> element.height;
		};
	}
	
	private void setRelativePositionAlong(Element element, FlexElement.Direction dir, int pos) {
		switch(dir) {
			case ROW -> element.relativeX = pos;
			case COLUMN -> element.relativeY = pos;
		}
	}
	
	public enum Direction {
		ROW,
		COLUMN,
		//row_reverse, column_reverse too
		;
		
		//probably not how to actually do this! yay
		public Direction crossAxis() {
			if(this == ROW) return COLUMN;
			else return ROW;
		}
	}
	
	///
	
	public static void main(String[] args) {
		System.out.println("----");
		
		{
			SizedElement a = new SizedElement("a", 20, 30);
			SizedElement b = new SizedElement("b", 30, 20);
			SizedElement c = new SizedElement("c", 5, 5);
			c.alignSelf = Alignment.START;
			
			FlexElement flex = new FlexElement("flex");
			flex.children.addAll(List.of(a, b, c));
			flex.mainAxisAlignment = Alignment.CENTER;
			flex.crossAxisAlignment = Alignment.END;
			
			flex.solve(new Constraints(0, 300, 0, 300));
			flex.dump();
		}
		
		System.out.println("----");
		
		{
			FlexElement flex = new FlexElement("flex");
			SizedElement left = new SizedElement("left", 30, 30);
			left.flexGrow = 2;
			SizedElement right = new SizedElement("right", 30, 30);
			right.flexGrow = 1;
			
			flex.children.add(left);
			flex.children.add(right);
			
			flex.solve(Constraints.tight(800, 600));
			flex.dump();
		}
		
		System.out.println("----");
	}
}