package agency.highlysuspect.incorporeal.client.flex;

import java.util.ArrayList;
import java.util.List;

public class FlexElement extends Element {
	public FlexElement(String name) {
		super(name);
	}
	
	public Direction flexDirection = Direction.ROW;
	public Alignment mainAxisAlignment = Alignment.CENTER; //justify-content
	public Alignment crossAxisAlignment = Alignment.CENTER; //align-items
	
	@Override
	public void solve(Constraints constraints, Layout layout) {
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
				child.solve(shrinkwrappingAlongFlexDirectionConstraints, layout);
				Layout.Result result = layout.get(child);
				usedMainAxisSpace += result.sizeAlong(flexDirection);
				usedCrossAxisSpace = Math.max(usedCrossAxisSpace, result.sizeAlong(crossDirection));
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
				
				child.solve(shrinkwrappingAlongFlexDirectionConstraints, layout);
				
				Layout.Result result = layout.get(child);
				usedMainAxisSpace += result.sizeAlong(flexDirection);
				usedCrossAxisSpace = Math.max(usedCrossAxisSpace, result.sizeAlong(crossDirection));
			}
			
			//Next, if there were any flexible children, distribute the remaining space to them. And solve their layout.
			if(totalFlexGrow != 0) {
				int spacePerFlexGrow = (totalMainAxisSpace - usedMainAxisSpace) / totalFlexGrow;
				for(Element child : flexibleChildren) {
					Constraints flexibleConstraints = constraints.withTightBoundsAlong(flexDirection, spacePerFlexGrow * child.flexGrow);
					child.solve(flexibleConstraints, layout);
					
					Layout.Result result = layout.get(child);
					usedMainAxisSpace += result.sizeAlong(flexDirection);
					usedCrossAxisSpace = Math.max(usedCrossAxisSpace, result.sizeAlong(crossDirection));
				}
			}
		}
		
		//B. Position each item along the main and cross axes.
		int remainingMainAxisSpace = shrinkwrapSelf ? 0 : totalMainAxisSpace - usedMainAxisSpace;
		int runningMainPos = switch(mainAxisAlignment) {
			case START -> 0;
			case CENTER -> remainingMainAxisSpace / 2;
			case END -> remainingMainAxisSpace;
		};
		
		for(Element child : children) {
			Layout.Result result = layout.get(child);
			result.setRelativePositionAlong(flexDirection, runningMainPos);
			runningMainPos += result.sizeAlong(flexDirection);
			
			int remainingCrossAxisSpace = totalCrossAxisSpace - result.sizeAlong(crossDirection);
			result.setRelativePositionAlong(crossDirection, switch(child.alignSelf != null ? child.alignSelf : crossAxisAlignment) {
				case START -> 0;
				case CENTER -> remainingCrossAxisSpace / 2;
				case END -> remainingCrossAxisSpace;
			});
		}
		
		//C. Set my own size
		int myWidth = flexDirection == Direction.ROW ? usedMainAxisSpace : usedCrossAxisSpace;
		int myHeight = flexDirection == Direction.ROW ? usedCrossAxisSpace : usedMainAxisSpace;
		layout.setSize(this, myWidth, myHeight);
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
	
	public enum Alignment {
		START,
		CENTER,
		END
	}
	
	///
	
	public static void main(String[] args) {
		System.out.println("----");
		
		{
			RigidElement a = new RigidElement("a", 20, 30);
			RigidElement b = new RigidElement("b", 30, 20);
			RigidElement c = new RigidElement("c", 5, 5);
			c.alignSelf = Alignment.START;
			
			FlexElement flex = new FlexElement("flex");
			flex.children.addAll(List.of(a, b, c));
			flex.mainAxisAlignment = Alignment.CENTER;
			flex.crossAxisAlignment = Alignment.END;
			
			Layout layout = new Layout();
			flex.solve(new Constraints(0, 300, 0, 300), layout);
			layout.dump();
		}
		
		System.out.println("----");
		
		{
			FlexElement flex = new FlexElement("flex");
			RigidElement left = new RigidElement("left", 30, 30);
			left.flexGrow = 2;
			RigidElement right = new RigidElement("right", 30, 30);
			right.flexGrow = 1;
			
			flex.children.add(left);
			flex.children.add(right);
			
			Layout layout = new Layout();
			flex.solve(Constraints.tight(800, 600), layout);
			layout.dump();
		}
		
		System.out.println("----");
	}
}