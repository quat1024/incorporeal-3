package agency.highlysuspect.incorporeal.client.flex;

//very simple element with an intrinsic width/height
//currently used in testing so i have something
public class RigidElement extends Element {
	public RigidElement(String name, int width, int height) {
		super(name);
		this.width = width;
		this.height = height;
	}
	
	int width, height;
	
	@Override
	public void solve(Constraints constraints, Layout layout) {
		for(Element child : children) child.solve(constraints, layout);
		layout.setSize(this, Math.max(constraints.minWidth(), width), Math.max(constraints.minHeight(), height));
	}
}
