package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.item.BoundEnderPearlItem;
import agency.highlysuspect.incorporeal.item.ShinyItem;
import agency.highlysuspect.incorporeal.item.FracturedSpaceRodItem;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.block.SpecialFlowerBlockItem;
import vazkii.botania.common.item.block.TinyPotatoBlockItem;
import vazkii.botania.common.item.equipment.bauble.ManaseerMonocleItem;

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
	public static final SpecialFlowerBlockItem SANVOCALIA = new SpecialFlowerBlockItem(IncBlocks.SANVOCALIA, props());
	public static final SpecialFlowerBlockItem SANVOCALIA_SMALL = new SpecialFlowerBlockItem(IncBlocks.SANVOCALIA_SMALL, props());
	public static final SpecialFlowerBlockItem FLOATING_SANVOCALIA = new SpecialFlowerBlockItem(IncBlocks.FLOATING_SANVOCALIA, props());
	public static final SpecialFlowerBlockItem FLOATING_SANVOCALIA_SMALL = new SpecialFlowerBlockItem(IncBlocks.FLOATING_SANVOCALIA_SMALL, props());
	
	public static final SpecialFlowerBlockItem FUNNY = new SpecialFlowerBlockItem(IncBlocks.FUNNY, props());
	public static final SpecialFlowerBlockItem FUNNY_SMALL = new SpecialFlowerBlockItem(IncBlocks.FUNNY_SMALL, props());
	public static final SpecialFlowerBlockItem FLOATING_FUNNY = new SpecialFlowerBlockItem(IncBlocks.FLOATING_FUNNY, props());
	public static final SpecialFlowerBlockItem FLOATING_FUNNY_SMALL = new SpecialFlowerBlockItem(IncBlocks.FLOATING_FUNNY_SMALL, props());
	
	//unstable cubes
	public static final Map<DyeColor, BlockItem> UNSTABLE_CUBES = Inc.sixteenColors(
		color -> new BlockItem(IncBlocks.UNSTABLE_CUBES.get(color), props()));
	
	//clearly, and other silliness
	public static final BlockItem CLEARLY = new BlockItem(IncBlocks.CLEARLY, props());
	public static final Map<DyeColor, BlockItem> PETAL_CARPETS = Inc.sixteenColors(
		color -> new BlockItem(IncBlocks.PETAL_CARPETS.get(color), props()));
	
	//taters
	public static final Map<Integer, TinyPotatoBlockItem> COMPRESSED_TATERS = new LinkedHashMap<>();
	static {
		IncBlocks.COMPRESSED_TATERS.forEach((level, block) ->
			COMPRESSED_TATERS.put(level, new TinyPotatoBlockItem(block, props().rarity(level == 8 ? Rarity.EPIC : Rarity.UNCOMMON))));
	}
	
	/// computer ///
	//crafting and misc
	public static final ShinyItem ENTERBRILLIANCE = new ShinyItem(props().rarity(Rarity.RARE));
	public static final Item COMPUTATIONAL_LENS_PATTERN = new Item(props().stacksTo(16));
	public static ManaseerMonocleItem DATA_MONOCLE; //Gets constructed late, see other comment
	
	//Capability stuff.
	public static final Map<Item, Function<ItemStack, CoordBoundItem>> COORD_BOUND_ITEM_MAKERS = Map.of( //(N.B: Map.of caps at 10 entries)
		FRACTURED_SPACE_ROD, FracturedSpaceRodItem.CoordBoundItem::new
	);
	
	public static final Map<Item, Function<ItemStack, ManaItem>> MANA_ITEM_MAKERS = Map.of(
		BOUND_ENDER_PEARL, stack -> new BoundEnderPearlItem.ManaItem(BOUND_ENDER_PEARL, stack)
	);
	
	public static void register(BiConsumer<Item, ResourceLocation> rRaw) {
		//Instantiating any ItemBauble explodes if it happens before EquipmentHandler is ready.
		//In practice, this only happens in dev instances on Fabric if we get unlucky and load before Botania.
		//These days EquipmentHandler.init is sorta idempotent. It's ok if I call it and then botania calls it as part of its init process.
		EquipmentHandler.init();
		DATA_MONOCLE = new ManaseerMonocleItem(props().stacksTo(1));
		
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
		
		//clearly, and other silliness
		r.acceptBlockItem(CLEARLY);
		r.acceptBlockItems(PETAL_CARPETS.values());
		
		//Taters
		r.acceptBlockItems(COMPRESSED_TATERS.values());
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
