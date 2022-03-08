package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.item.block.ItemBlockTinyPotato;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class IncItems {
	//non-blockitems
	public static final CorporeaTicketItem CORPOREA_TICKET = new CorporeaTicketItem(props());
	public static final TicketConjurerItem TICKET_CONJURER = new TicketConjurerItem(props().stacksTo(1));
	public static final Item SOUL_CORE_FRAME = new Item(props()); //TODO: teisr (ugh)
	public static final Item FRACTURED_SPACE_ROD = new FracturedSpaceRodItem(props().stacksTo(1));
	
	//corporetics
	public static final BlockItem CORPOREA_SOLIDIFIER = new BlockItem(IncBlocks.CORPOREA_SOLIDIFIER, props());
	public static final BlockItem RED_STRING_LIAR = new BlockItem(IncBlocks.RED_STRING_LIAR, props());
	public static final BlockItem RED_STRING_CONSTRICTOR = new BlockItem(IncBlocks.RED_STRING_CONSTRICTOR, props());
	public static final BlockItem FRAME_TINKERER = new BlockItem(IncBlocks.FRAME_TINKERER, props());
	
	//soul cores
	public static final BlockItem ENDER_SOUL_CORE = new IncBlockItemWithTEISR(IncBlocks.ENDER_SOUL_CORE, props());
	public static final BlockItem POTION_SOUL_CORE = new IncBlockItemWithTEISR(IncBlocks.POTION_SOUL_CORE, props());
	
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
	
	//taters
	public static final Map<Integer, ItemBlockTinyPotato> COMPRESSED_TATERS = new LinkedHashMap<>();
	static {
		IncBlocks.COMPRESSED_TATERS.forEach((level, block) ->
			COMPRESSED_TATERS.put(level, new ItemBlockTinyPotato(block, props().rarity(level == 8 ? Rarity.EPIC : Rarity.UNCOMMON))));
	}
	
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
		r.accept(FRACTURED_SPACE_ROD, Inc.id("fractured_space_rod"));
		
		//generic blockitems
		registerBlockItems(r, List.of(
			//corporetics
			CORPOREA_SOLIDIFIER, RED_STRING_LIAR, RED_STRING_CONSTRICTOR, FRAME_TINKERER,
			//soul cores
			ENDER_SOUL_CORE, POTION_SOUL_CORE,
			//natural devices
			NATURAL_REPEATER, NATURAL_COMPARATOR,
			//clearly
			CLEARLY,
			//computer
			DATA_PRISM, DATA_STORAGE, MATCHER_LENS, NUMBER_LENS, NEGATORY_LENS),
			//unstable cubes
			UNSTABLE_CUBES.values(),
			//taters
			COMPRESSED_TATERS.values()
		);
		
		//flowers
		r.accept(SANVOCALIA, Inc.id("sanvocalia"));
		r.accept(SANVOCALIA_SMALL, Inc.id("sanvocalia_chibi"));
		r.accept(FLOATING_SANVOCALIA, Inc.id("floating_sanvocalia"));
		r.accept(FLOATING_SANVOCALIA_SMALL, Inc.id("floating_sanvocalia_chibi"));
		
		r.accept(FUNNY, Inc.id("funny"));
		r.accept(FUNNY_SMALL, Inc.id("funny_chibi"));
		r.accept(FLOATING_FUNNY, Inc.id("floating_funny"));
		r.accept(FLOATING_FUNNY_SMALL, Inc.id("floating_funny_chibi"));
	}
	
	@SafeVarargs
	private static void registerBlockItems(BiConsumer<Item, ResourceLocation> r, Collection<? extends BlockItem>... collections) {
		for(Collection<? extends BlockItem> collection : collections) {
			for(BlockItem bi : collection) {
				//TODO: this needed?
				Item.BY_BLOCK.put(bi.getBlock(), bi);
				r.accept(bi, Registry.BLOCK.getKey(bi.getBlock()));
			}
		}
	}
	
	private static Item.Properties props() {
		return new Item.Properties().tab(IncXplat.INSTANCE.getCreativeTab());
	}
}
