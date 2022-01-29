package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncItems;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class IncFabricCreativeTab extends CreativeModeTab {
	public static final IncFabricCreativeTab INSTANCE = new IncFabricCreativeTab();
	
	public IncFabricCreativeTab() {
		super(computeIndex(), Inc.MODID);
		hideTitle();
		setBackgroundSuffix("incorporeal.png");
	}
	
	private static int computeIndex() {
		((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
		return CreativeModeTab.TABS.length - 1;
	}
	
	@Override
	public ItemStack makeIcon() {
		return new ItemStack(IncItems.CORPOREA_TICKET);
	}
}
