package agency.highlysuspect.incorporeal.client.flex;

public enum Alignment {
	START,
	CENTER,
	END;
	
	//no idea what to call this method but it crops up a lot
	public int florp(int remainingSpace) {
		return switch(this) {
			case START -> 0;
			case CENTER -> remainingSpace / 2;
			case END -> remainingSpace;
		};
	}
}
