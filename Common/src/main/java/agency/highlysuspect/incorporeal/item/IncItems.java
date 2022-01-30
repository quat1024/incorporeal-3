package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;

public class IncItems {
	public static final CorporeaTicketItem CORPOREA_TICKET = new CorporeaTicketItem(props());
	
	public static final BlockItem CORPOREA_SOLIDIFIER = new BlockItem(IncBlocks.CORPOREA_SOLIDIFIER, props());
	public static final BlockItem RED_STRING_LIAR = new BlockItem(IncBlocks.RED_STRING_LIAR, props());
	public static final BlockItem FRAME_TINKERER = new BlockItem(IncBlocks.FRAME_TINKERER, props());
	public static final BlockItem CORPOREA_RETAINER_EVAPORATOR = new BlockItem(IncBlocks.CORPOREA_RETAINER_EVAPORATOR, props());
	
	private static Item.Properties props() {
		return IncXplat.INSTANCE.defaultItemProperties();
	}
	
	private static Item.Properties unstackable() {
		return props().stacksTo(1);
	}
	
	public static void register(BiConsumer<Item, ResourceLocation> r) {
		r.accept(CORPOREA_TICKET, Inc.id("corporea_ticket"));
		
		blockItem(r, CORPOREA_SOLIDIFIER);
		blockItem(r, RED_STRING_LIAR);
		blockItem(r, FRAME_TINKERER);
		blockItem(r, CORPOREA_RETAINER_EVAPORATOR);
	}
	
	private static void blockItem(BiConsumer<Item, ResourceLocation> r, BlockItem bi) {
		//TODO: this needed?
		Item.BY_BLOCK.put(bi.getBlock(), bi);
		r.accept(bi, Registry.BLOCK.getKey(bi.getBlock()));
	}
}
