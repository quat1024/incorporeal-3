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

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public class IncItems {
	//non-blockitems
	public static final CorporeaTicketItem CORPOREA_TICKET = new CorporeaTicketItem(props());
	public static final TicketConjurerItem TICKET_CONJURER = new TicketConjurerItem(props().stacksTo(1));
	public static final Item SOUL_CORE_FRAME = new Item(props()); //TODO: teisr (ugh)
	
	//corporetics
	public static final BlockItem CORPOREA_SOLIDIFIER = new BlockItem(IncBlocks.CORPOREA_SOLIDIFIER, props());
	public static final BlockItem RED_STRING_LIAR = new BlockItem(IncBlocks.RED_STRING_LIAR, props());
	public static final BlockItem FRAME_TINKERER = new BlockItem(IncBlocks.FRAME_TINKERER, props());
	public static final BlockItem CORPOREA_RETAINER_EVAPORATOR = new BlockItem(IncBlocks.CORPOREA_RETAINER_EVAPORATOR, props());
	
	//soul cores
	public static final BlockItem ENDER_SOUL_CORE = new IncBlockItemWithTEISR(IncBlocks.ENDER_SOUL_CORE, props());
	
	//natural devices
	public static final BlockItem NATURAL_REPEATER = new BlockItem(IncBlocks.NATURAL_REPEATER, props());
	public static final BlockItem NATURAL_COMPARATOR = new BlockItem(IncBlocks.NATURAL_COMPARATOR, props());
	
	//flowers
	public static final ItemBlockSpecialFlower SANVOCALIA = new ItemBlockSpecialFlower(IncBlocks.SANVOCALIA, props());
	public static final ItemBlockSpecialFlower SANVOCALIA_SMALL = new ItemBlockSpecialFlower(IncBlocks.SANVOCALIA_SMALL, props());
	public static final ItemBlockSpecialFlower FLOATING_SANVOCALIA = new ItemBlockSpecialFlower(IncBlocks.FLOATING_SANVOCALIA, props());
	public static final ItemBlockSpecialFlower FLOATING_SANVOCALIA_SMALL = new ItemBlockSpecialFlower(IncBlocks.FLOATING_SANVOCALIA_SMALL, props());
	
	public static final ItemBlockSpecialFlower FUNNY = new ItemBlockSpecialFlower(IncBlocks.FUNNY, props());
	public static final ItemBlockSpecialFlower FUNNY_SMALL = new ItemBlockSpecialFlower(IncBlocks.FUNNY_SMALL, props());
	public static final ItemBlockSpecialFlower FLOATING_FUNNY = new ItemBlockSpecialFlower(IncBlocks.FLOATING_FUNNY, props());
	public static final ItemBlockSpecialFlower FLOATING_FUNNY_SMALL = new ItemBlockSpecialFlower(IncBlocks.FLOATING_FUNNY_SMALL, props());
	
	//unstable cubes
	public static final Map<DyeColor, BlockItem> UNSTABLE_CUBES = Inc.sixteenColors(
		color -> new IncBlockItemWithTEISR(IncBlocks.UNSTABLE_CUBES.get(color), props()));
	
	//Clearly
	public static final BlockItem CLEARLY = new BlockItem(IncBlocks.CLEARLY, props());
	
	//computer
	public static final BlockItem DATA_PRISM = new BlockItem(IncBlocks.DATA_PRISM, props());
	public static final BlockItem DATA_STORAGE = new BlockItem(IncBlocks.DATA_STORAGE, props());
	public static final BlockItem MATCHER_LENS = new BlockItem(IncBlocks.MATCHER_LENS, props());
	public static final BlockItem NUMBER_LENS = new BlockItem(IncBlocks.NUMBER_LENS, props());
	public static final BlockItem NEGATORY_LENS = new BlockItem(IncBlocks.NEGATORY_LENS, props());
	
	public static void register(BiConsumer<Item, ResourceLocation> r) {
		//items
		r.accept(CORPOREA_TICKET, Inc.id("corporea_ticket"));
		r.accept(TICKET_CONJURER, Inc.id("ticket_conjurer"));
		r.accept(SOUL_CORE_FRAME, Inc.id("soul_core_frame"));
		
		//blockitems
		registerBlockItems(r,
			CORPOREA_SOLIDIFIER, RED_STRING_LIAR, FRAME_TINKERER, CORPOREA_RETAINER_EVAPORATOR,
			ENDER_SOUL_CORE,
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
		registerBlockItems(r, UNSTABLE_CUBES.values());
		
		//clearly
		registerBlockItems(r, CLEARLY);
		
		//computer
		registerBlockItems(r, DATA_PRISM, DATA_STORAGE, MATCHER_LENS, NUMBER_LENS, NEGATORY_LENS);
	}
	
	private static void registerBlockItems(BiConsumer<Item, ResourceLocation> r, BlockItem... bis) {
		for(BlockItem bi : bis) {
			//TODO: this needed?
			Item.BY_BLOCK.put(bi.getBlock(), bi);
			r.accept(bi, Registry.BLOCK.getKey(bi.getBlock()));
		}
	}
	
	//ah, java.
	//never change
	private static void registerBlockItems(BiConsumer<Item, ResourceLocation> r, Collection<BlockItem> bis) {
		registerBlockItems(r, bis.toArray(BlockItem[]::new));
	}
	
	private static Item.Properties props() {
		return new Item.Properties().tab(IncXplat.INSTANCE.getCreativeTab());
	}
}
