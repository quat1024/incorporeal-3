package agency.highlysuspect.incorporeal.client.flex;

import java.util.function.Consumer;

/**
 * this element:
 *   - expands to be as large as possible
 *   - solves its child element as small as possible
 *   - aligns the child element inside itself using the specified alignment settings
 * Actually it supports multiple children and just stacks them, smh
 */
public class AligningElement extends Element {
	public AligningElement(String name) {
		super(name);
	}
	
	public Alignment horizontalAlignment;
	public Alignment verticaliAlignment;
	
	private static final Constraints unbounded = Constraints.infinite();
	
	public Element child;
	
	@Override
	public void solve(Constraints constraints) {
		if(child == null) return;
		
		child.solve(unbounded); //always use unbounded constraints on the child so its as small as possible
		child.relativeX = constraints.isInfiniteWidth() ? 0 : horizontalAlignment.florp(constraints.maxWidth() - child.width);
		child.relativeY = constraints.isInfiniteHeight() ? 0 : verticaliAlignment.florp(constraints.maxHeight() - child.height);
		width = constraints.clampWidth(constraints.isInfiniteWidth() ? child.width : constraints.maxWidth());
		height = constraints.clampHeight(constraints.isInfiniteHeight() ? child.height : constraints.maxHeight());
	}
	
	@Override
	public void visitChildren(Consumer<Element> visitor) {
		visitor.accept(child);
	}
}
