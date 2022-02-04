package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.Map;
import java.util.function.BiConsumer;

public class IncItems {
	public static final CorporeaTicketItem CORPOREA_TICKET = new CorporeaTicketItem(props());
	
	public static final BlockItem CORPOREA_SOLIDIFIER = new BlockItem(IncBlocks.CORPOREA_SOLIDIFIER, props());
	public static final BlockItem RED_STRING_LIAR = new BlockItem(IncBlocks.RED_STRING_LIAR, props());
	public static final BlockItem FRAME_TINKERER = new BlockItem(IncBlocks.FRAME_TINKERER, props());
	public static final BlockItem CORPOREA_RETAINER_EVAPORATOR = new BlockItem(IncBlocks.CORPOREA_RETAINER_EVAPORATOR, props());
	
	public static final BlockItem NATURAL_REPEATER = new BlockItem(IncBlocks.NATURAL_REPEATER, props());
	public static final BlockItem NATURAL_COMPARATOR = new BlockItem(IncBlocks.NATURAL_COMPARATOR, props());
	
	public static final ItemBlockSpecialFlower SANVOCALIA = new ItemBlockSpecialFlower(IncBlocks.SANVOCALIA, props());
	public static final ItemBlockSpecialFlower SANVOCALIA_SMALL = new ItemBlockSpecialFlower(IncBlocks.SANVOCALIA_SMALL, props());
	public static final ItemBlockSpecialFlower FLOATING_SANVOCALIA = new ItemBlockSpecialFlower(IncBlocks.FLOATING_SANVOCALIA, props());
	public static final ItemBlockSpecialFlower FLOATING_SANVOCALIA_SMALL = new ItemBlockSpecialFlower(IncBlocks.FLOATING_SANVOCALIA_SMALL, props());
	
	public static final ItemBlockSpecialFlower FUNNY = new ItemBlockSpecialFlower(IncBlocks.FUNNY, props());
	public static final ItemBlockSpecialFlower FUNNY_SMALL = new ItemBlockSpecialFlower(IncBlocks.FUNNY_SMALL, props());
	public static final ItemBlockSpecialFlower FLOATING_FUNNY = new ItemBlockSpecialFlower(IncBlocks.FLOATING_FUNNY, props());
	public static final ItemBlockSpecialFlower FLOATING_FUNNY_SMALL = new ItemBlockSpecialFlower(IncBlocks.FLOATING_FUNNY_SMALL, props());
	
	public static final Map<DyeColor, BlockItem> UNSTABLE_CUBES = Inc.sixteenColors(
		color -> new IncBlockItemWithTEISR(IncBlocks.UNSTABLE_CUBES.get(color), props()));
	
	public static void register(BiConsumer<Item, ResourceLocation> r) {
		//items
		r.accept(CORPOREA_TICKET, Inc.id("corporea_ticket"));
		
		//blockitems
		registerBlockItems(r,
			CORPOREA_SOLIDIFIER, RED_STRING_LIAR, FRAME_TINKERER, CORPOREA_RETAINER_EVAPORATOR,
			NATURAL_REPEATER, NATURAL_COMPARATOR);
		
		//flowers
		r.accept(SANVOCALIA, Inc.id("sanvocalia"));
		r.accept(SANVOCALIA_SMALL, Inc.id("sanvocalia_chibi"));
		r.accept(FLOATING_SANVOCALIA, Inc.id("floating_sanvocalia"));
		r.accept(FLOATING_SANVOCALIA_SMALL, Inc.id("floating_sanvocalia_chibi"));
		
		r.accept(FUNNY, Inc.id("funny"));
		r.accept(FUNNY_SMALL, Inc.id("funny_chibi"));
		r.accept(FLOATING_FUNNY, Inc.id("floating_funny"));
		r.accept(FLOATING_FUNNY_SMALL, Inc.id("floating_funny_chibi"));
		
		//unstable cubes
		UNSTABLE_CUBES.values().forEach(item -> registerBlockItem(r, item));
	}
	
	private static void registerBlockItems(BiConsumer<Item, ResourceLocation> r, BlockItem... bis) {
		for(BlockItem bi : bis) registerBlockItem(r, bi);
	}
	
	private static void registerBlockItem(BiConsumer<Item, ResourceLocation> r, BlockItem bi) {
		//TODO: this needed?
		Item.BY_BLOCK.put(bi.getBlock(), bi);
		r.accept(bi, Registry.BLOCK.getKey(bi.getBlock()));
	}
	
	private static Item.Properties props() {
		return IncXplat.INSTANCE.defaultItemProperties();
	}
}
