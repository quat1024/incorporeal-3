package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import vazkii.botania.client.render.tile.TEISR;
import vazkii.botania.network.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IncClientItemProperties {
	public static void registerPropertyOverrides(TriConsumer<ItemLike, ResourceLocation, ClampedItemPropertyFunction> r) {
		//Returns 1 if the corporea ticket has a request written on it, and 0 otherwise.
		//Now that I think about it, it's pretty much impossible to have a ticket with nothing written on it...
		r.accept(IncItems.CORPOREA_TICKET, Inc.id("written_ticket"),
			(stack, level, ent, seed) -> IncItems.CORPOREA_TICKET.hasRequest(stack) ? 1 : 0);
	}
	
	//Botania has something like this too, in EntityRenderers, but it's not meant to be extended. So I will reimplement it myself.
	public static final Map<Block, Function<Block, TEISR>> BE_ITEM_RENDERER_FACTORIES = new HashMap<>();
	static {
		IncBlocks.UNSTABLE_CUBES.forEach((color, cube) -> BE_ITEM_RENDERER_FACTORIES.put(cube, TEISR::new));
	}
}
