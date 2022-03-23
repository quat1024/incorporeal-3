package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.computer.TicketConjurerItem;
import agency.highlysuspect.incorporeal.computer.TicketItem;
import agency.highlysuspect.incorporeal.computer.NotManaLens;
import agency.highlysuspect.incorporeal.computer.types.DataLenses;
import agency.highlysuspect.incorporeal.item.FracturedSpaceRodItem;
import agency.highlysuspect.incorporeal.platform.IncBlockItemWithCoolRenderer;
import agency.highlysuspect.incorporeal.platform.IncItemWithCoolRenderer;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import vazkii.botania.api.item.ICoordBoundItem;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.item.block.ItemBlockTinyPotato;
import vazkii.botania.common.item.lens.ItemLens;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class IncItems {
	//creative tab!
	public static final CreativeModeTab TAB = IncXplat.INSTANCE.createCreativeTab();
	
	//corporetics
	public static final Item FRACTURED_SPACE_ROD = new FracturedSpaceRodItem(props().stacksTo(1));
	public static final BlockItem CORPOREA_SOLIDIFIER = new BlockItem(IncBlocks.CORPOREA_SOLIDIFIER, props());
	public static final BlockItem RED_STRING_LIAR = new BlockItem(IncBlocks.RED_STRING_LIAR, props());
	public static final BlockItem RED_STRING_CONSTRICTOR = new BlockItem(IncBlocks.RED_STRING_CONSTRICTOR, props());
	public static final BlockItem FRAME_TINKERER = new BlockItem(IncBlocks.FRAME_TINKERER, props());
	
	//soul cores
	public static final Item SOUL_CORE_FRAME = new IncItemWithCoolRenderer(props());
	public static final BlockItem ENDER_SOUL_CORE = new IncBlockItemWithCoolRenderer(IncBlocks.ENDER_SOUL_CORE, props());
	public static final BlockItem POTION_SOUL_CORE = new IncBlockItemWithCoolRenderer(IncBlocks.POTION_SOUL_CORE, props());
	
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
		color -> new IncBlockItemWithCoolRenderer(IncBlocks.UNSTABLE_CUBES.get(color), props()));
	
	//Clearly
	public static final BlockItem CLEARLY = new BlockItem(IncBlocks.CLEARLY, props());
	
	//taters
	public static final Map<Integer, ItemBlockTinyPotato> COMPRESSED_TATERS = new LinkedHashMap<>();
	static {
		IncBlocks.COMPRESSED_TATERS.forEach((level, block) ->
			COMPRESSED_TATERS.put(level, new ItemBlockTinyPotato(block, props().rarity(level == 8 ? Rarity.EPIC : Rarity.UNCOMMON))));
	}
	
	//computer
	public static final TicketItem TICKET = new TicketItem(props());
	public static final TicketConjurerItem TICKET_CONJURER = new TicketConjurerItem(props().stacksTo(1));
	
	public static final BlockItem DATA_FUNNEL = new BlockItem(IncBlocks.DATA_FUNNEL, props());
	public static final ItemLens NUMBER_LENS = new ItemLens(props(), new NotManaLens(DataLenses.number), 0);
	public static final ItemLens MATCHER_LENS = new ItemLens(props(), new NotManaLens(DataLenses.matcher), 0);
	public static final ItemLens NEGATING_LENS = new ItemLens(props(), new NotManaLens(DataLenses.negating), 0);
	public static final BlockItem DATASTONE_BLOCK = new BlockItem(IncBlocks.DATASTONE_BLOCK, props());
	public static final BlockItem POINTED_DATASTONE = new BlockItem(IncBlocks.POINTED_DATASTONE, props());
	
	//Capability stuff.
	public static final Map<Item, Function<ItemStack, ICoordBoundItem>> COORD_BOUND_ITEM_MAKERS = Map.of( //(N.B: Map.of caps at 10 entries)
		FRACTURED_SPACE_ROD, FracturedSpaceRodItem.CoordBoundItem::new
	);
	
	public static void register(BiConsumer<Item, ResourceLocation> rRaw) {
		ItemRegistrar r = rRaw::accept;
		
		//Note that item registry order is significant, particularly when determining the ordering of the creative tab.
		//(Forge likes to try and keep the ordering the same between runs, Fabric always displays raw registration order.)
		//So, register the items in a logical, categorical order.
		
		//corporetics
		r.accept(FRACTURED_SPACE_ROD, Inc.id("fractured_space_rod"));
		r.acceptBlockItems(
			CORPOREA_SOLIDIFIER,
			RED_STRING_LIAR,
			RED_STRING_CONSTRICTOR,
			FRAME_TINKERER
		);
		
		//soul cores
		r.accept(SOUL_CORE_FRAME, Inc.id("soul_core_frame"));
		r.acceptBlockItems(
			ENDER_SOUL_CORE,
			POTION_SOUL_CORE
		);
		
		//natural devices
		r.acceptBlockItems(
			NATURAL_REPEATER,
			NATURAL_COMPARATOR
		);
		
		//flowers
		r.acceptBlockItems(
			SANVOCALIA,
			SANVOCALIA_SMALL,
			FLOATING_SANVOCALIA,
			FLOATING_SANVOCALIA_SMALL,
			FUNNY,
			FUNNY_SMALL,
			FLOATING_FUNNY,
			FLOATING_FUNNY_SMALL
		);
		
		//unstable cubes
		r.acceptBlockItems(UNSTABLE_CUBES.values());
		
		//Clearly
		r.acceptBlockItem(CLEARLY);
		
		//Taters
		r.acceptBlockItems(COMPRESSED_TATERS.values());
		
		//Computer
		r.accept(TICKET, Inc.id("ticket"));
		r.accept(TICKET_CONJURER, Inc.id("ticket_conjurer"));
		
		r.acceptBlockItem(DATA_FUNNEL);
		r.accept(NUMBER_LENS, Inc.id("number_lens"));
		r.accept(MATCHER_LENS, Inc.id("matcher_lens"));
		r.accept(NEGATING_LENS, Inc.id("negating_lens"));
		
		r.acceptBlockItems(
			DATASTONE_BLOCK,
			POINTED_DATASTONE
		);
	}
	
	private static Item.Properties props() {
		return new Item.Properties().tab(TAB);
	}
	
	//BlockItems can be a little bit error-prone:
	//- BlockItems traditionally use the same ResourceLocation as their block. This is not a hard requirement.
	//  acceptBlockItem does this for you.
	//- The Item.BY_BLOCK map is not automatically populated. Bad things may happen if you don't populate it.
	//  The only way to break this invariant with ItemRegistrar is by calling acceptRaw directly. 
	public interface ItemRegistrar {
		void acceptRaw(Item i, ResourceLocation id);
		
		default void accept(Item i, ResourceLocation id) {
			if(i instanceof BlockItem bi) Item.BY_BLOCK.put(bi.getBlock(), bi);
			acceptRaw(i, id);
		}
		
		default void acceptBlockItem(BlockItem bi) {
			accept(bi, Registry.BLOCK.getKey(bi.getBlock()));
		}
		
		default void acceptBlockItems(BlockItem... bis) {
			for(BlockItem bi : bis) acceptBlockItem(bi);
		}
		
		default void acceptBlockItems(Collection<? extends BlockItem> bis) {
			for(BlockItem bi : bis) acceptBlockItem(bi);
		}
	}
	
	public static final Item CREATIVE_MODE_TAB_ICON = ENDER_SOUL_CORE;
}
