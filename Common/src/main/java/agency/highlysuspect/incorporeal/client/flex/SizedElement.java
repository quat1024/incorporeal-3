package agency.highlysuspect.incorporeal.client.flex;

//An element that tries to be exactly a certain size; no guarantees tho
public class SizedElement extends Element {
	public SizedElement(String name, int preferredWidth, int preferredHeight) {
		super(name);
		this.preferredWidth = preferredWidth;
		this.preferredHeight = preferredHeight;
	}
	
	int preferredWidth, preferredHeight;
	
	@Override
	public void solve(Constraints constraints) {
		width = constraints.clampWidth(preferredWidth);
		height = constraints.clampHeight(preferredHeight);
	}
}
