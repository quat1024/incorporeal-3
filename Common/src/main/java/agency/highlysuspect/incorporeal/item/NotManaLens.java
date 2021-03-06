package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.computer.types.DataLens;
import vazkii.botania.common.item.lens.Lens;

/**
 * A class that's just Lensy enough to let you make ItemLenses out of it, but doesn't actually do anything to mana bursts.
 * Instead, it conceptually wraps a DataLens, because I'm overloading what the Mana Prism does in this mod. Gottem.
 * "new Lens()" on its own does create a simple lens that does nothing, so that's helpful.
 */
public class NotManaLens extends Lens {
	public NotManaLens(DataLens dataLens) {
		this.dataLens = dataLens;
	}
	
	public final DataLens dataLens;
	
	public DataLens getDataLens() {
		return dataLens;
	}
}
