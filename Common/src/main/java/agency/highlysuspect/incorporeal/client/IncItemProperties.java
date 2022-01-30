package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import vazkii.botania.network.TriConsumer;

public class IncItemProperties {
	public static void register(TriConsumer<ItemLike, ResourceLocation, ClampedItemPropertyFunction> r) {
		//Returns 1 if the corporea ticket has a request written on it, and 0 otherwise.
		//Now that I think about it, it's pretty much impossible to have a ticket with nothing written on it...
		r.accept(IncItems.CORPOREA_TICKET, Inc.id("written_ticket"),
			(stack, level, ent, seed) -> IncItems.CORPOREA_TICKET.hasRequest(stack) ? 1 : 0);
	}
}
