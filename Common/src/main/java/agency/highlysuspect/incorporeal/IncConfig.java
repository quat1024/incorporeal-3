package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.platform.ConfigBuilder;

import java.util.function.Supplier;

public class IncConfig {
	public IncConfig(ConfigBuilder builder) {
		builder.pushCategory("computer");
		pointedDatastoneIsPushable = builder.addBooleanProperty("pointedDatastoneIsPushable", false,
			"If 'true', Pointed Datastone will be pushable with a Piston. Else, it will break off when pushed, like a pumpkin.",
			"This REQUIRES some sort of 'movable block entity' mod to do anything!!!!!!!!!!!"
		);
		
		pointedDatastoneIsSticky = builder.addBooleanProperty("pointedDatastoneIsSticky", true,
			"If 'true', Pointed Datastone will stick to itself, allowing the entire stalactite to be moved with one piston.",
			"This REQUIRES pointedDatastoneIsPushable AND some sort of 'movable block entity' mod, to do anything!!!!!!!",
			"Also it is kind of experimental. I'm not really sure if this leads to interesting gameplay possibilities."
		);
		builder.popCategory();
		
		builder.pushCategory("silly");
		everyoneHearsSanvocalia = builder.addBooleanProperty("everyoneHearsSanvocalia", false,
			"Ok, so: the Sanvocalia functional flower has an easter-egg, where if it tries to request an item but isn't next to any corporea indices,",
			"it just dumps the request in the chat. This is a nod to when players try to use a Corporea Index but are standing too far away from it.",
			"...I first made this mod in like 2019, I'm sure it was funny then.",
			"Anyway, if this is 'true', everyone will see the message. If it's 'false', only the player who placed the flower will see the message.",
			"(Regardless, a line will be posted to the game log every time a message is sent in this manner, including the coordinates of the flower.)"
		);
		builder.popCategory();
		builder.finish();
	}
	
	private final Supplier<Boolean> pointedDatastoneIsPushable;
	private final Supplier<Boolean> pointedDatastoneIsSticky;
	private final Supplier<Boolean> everyoneHearsSanvocalia;
	
	public boolean pointedDatastoneIsPushable() {
		return pointedDatastoneIsPushable.get();
	}
	
	public boolean pointedDatastoneIsSticky() {
		return pointedDatastoneIsSticky.get();
	}
	
	public boolean everyoneHearsSanvocalia() {
		return everyoneHearsSanvocalia.get();
	}
}
