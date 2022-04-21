package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.computer.EmptyType;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.item.BoundEnderPearlItem;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import agency.highlysuspect.incorporeal.item.TicketItem;
import agency.highlysuspect.incorporeal.item.NotManaLens;
import agency.highlysuspect.incorporeal.computer.types.DataLenses;
import agency.highlysuspect.incorporeal.item.FracturedSpaceRodItem;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.api.item.ICoordBoundItem;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.item.block.ItemBlockTinyPotato;
import vazkii.botania.common.item.equipment.bauble.ItemMonocle;
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
	public static final BlockItem CORPOREA_PYLON = new BlockItem(IncBlocks.CORPOREA_PYLON, props());
	public static final BoundEnderPearlItem BOUND_ENDER_PEARL = new BoundEnderPearlItem(props().stacksTo(1));
	
	//soul cores
	public static final Item SOUL_CORE_FRAME = new Item(props());
	public static final BlockItem ENDER_SOUL_CORE = new BlockItem(IncBlocks.ENDER_SOUL_CORE, props());
	public static final BlockItem POTION_SOUL_CORE = new BlockItem(IncBlocks.POTION_SOUL_CORE, props());
	
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
		color -> new BlockItem(IncBlocks.UNSTABLE_CUBES.get(color), props()));
	
	//Clearly
	public static final BlockItem CLEARLY = new BlockItem(IncBlocks.CLEARLY, props());
	
	//taters
	public static final Map<Integer, ItemBlockTinyPotato> COMPRESSED_TATERS = new LinkedHashMap<>();
	static {
		IncBlocks.COMPRESSED_TATERS.forEach((level, block) ->
			COMPRESSED_TATERS.put(level, new ItemBlockTinyPotato(block, props().rarity(level == 8 ? Rarity.EPIC : Rarity.UNCOMMON))));
	}
	
	//computer
	public static final TicketItem<Unit> EMPTY_TICKET = new TicketItem<>(DataTypes.EMPTY, props());
	public static final TicketItem<Integer> INTEGER_TICKET = new TicketItem<>(DataTypes.INTEGER, props());
	public static final TicketItem<ICorporeaRequestMatcher> MATCHER_TICKET = new TicketItem<>(DataTypes.MATCHER, props());
	public static final TicketItem<SolidifiedRequest> SOLIDIFIED_REQUEST_TICKET = new TicketItem<>(DataTypes.SOLIDIFIED_REQUEST, props());
	
	public static final TicketConjurerItem<Unit> EMPTY_CONJURER = new TicketConjurerItem<>(DataTypes.EMPTY, props().stacksTo(1));
	public static final TicketConjurerItem<Integer> INTEGER_CONJURER = new TicketConjurerItem<>(DataTypes.INTEGER, props().stacksTo(1));
	public static final TicketConjurerItem<ICorporeaRequestMatcher> MATCHER_CONJURER = new TicketConjurerItem<>(DataTypes.MATCHER, props().stacksTo(1));
	public static final TicketConjurerItem<SolidifiedRequest> SOLIDIFIED_REQUEST_CONJURER = new TicketConjurerItem<>(DataTypes.SOLIDIFIED_REQUEST, props().stacksTo(1));
	
	public static final BlockItem DATA_FUNNEL = new BlockItem(IncBlocks.DATA_FUNNEL, props());
	public static final ItemLens NUMBER_LENS = new ItemLens(props(), new NotManaLens(DataLenses.number), 0);
	public static final ItemLens MATCHER_LENS = new ItemLens(props(), new NotManaLens(DataLenses.matcher), 0);
	public static final ItemLens NEGATING_LENS = new ItemLens(props(), new NotManaLens(DataLenses.negating), 0);
	public static final BlockItem DATASTONE_BLOCK = new BlockItem(IncBlocks.DATASTONE_BLOCK, props());
	public static final BlockItem POINTED_DATASTONE = new BlockItem(IncBlocks.POINTED_DATASTONE, props());
	public static ItemMonocle DATA_MONOCLE; //Gets constructed late, see other comment
	
	//Capability stuff.
	public static final Map<Item, Function<ItemStack, ICoordBoundItem>> COORD_BOUND_ITEM_MAKERS = Map.of( //(N.B: Map.of caps at 10 entries)
		FRACTURED_SPACE_ROD, FracturedSpaceRodItem.CoordBoundItem::new
	);
	
	public static final Map<Item, Function<ItemStack, IManaItem>> MANA_ITEM_MAKERS = Map.of(
		BOUND_ENDER_PEARL, stack -> new BoundEnderPearlItem.ManaItem(BOUND_ENDER_PEARL, stack)
	);
	
	public static void register(BiConsumer<Item, ResourceLocation> rRaw) {
		//Instantiating any ItemBauble explodes if it happens before EquipmentHandler is ready.
		//In practice, this only happens in dev instances on Fabric if we get unlucky and load before Botania.
		//These days EquipmentHandler.init is sorta idempotent. It's ok if I call it and then botania calls it as part of its init process.
		EquipmentHandler.init();
		DATA_MONOCLE = new ItemMonocle(props().stacksTo(1));
		
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
			FRAME_TINKERER,
			CORPOREA_PYLON
		);
		r.accept(BOUND_ENDER_PEARL, Inc.id("bound_ender_pearl"));
		
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
		r.accept(EMPTY_TICKET, Inc.id("empty_ticket"));
		r.accept(INTEGER_TICKET, Inc.id("integer_ticket"));
		r.accept(MATCHER_TICKET, Inc.id("matcher_ticket"));
		r.accept(SOLIDIFIED_REQUEST_TICKET, Inc.id("solidified_request_ticket"));
		r.accept(EMPTY_CONJURER, Inc.id("empty_conjurer"));
		r.accept(INTEGER_CONJURER, Inc.id("integer_conjurer"));
		r.accept(MATCHER_CONJURER, Inc.id("matcher_conjurer"));
		r.accept(SOLIDIFIED_REQUEST_CONJURER, Inc.id("solidified_request_conjurer"));
		
		r.acceptBlockItem(DATA_FUNNEL);
		r.accept(NUMBER_LENS, Inc.id("number_lens"));
		r.accept(MATCHER_LENS, Inc.id("matcher_lens"));
		r.accept(NEGATING_LENS, Inc.id("negating_lens"));
		
		r.accept(DATA_MONOCLE, Inc.id("data_monocle"));
		
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
