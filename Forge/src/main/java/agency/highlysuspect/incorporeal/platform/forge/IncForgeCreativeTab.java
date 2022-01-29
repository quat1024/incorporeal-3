package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class IncForgeCreativeTab extends CreativeModeTab {
	public static final IncForgeCreativeTab INSTANCE = new IncForgeCreativeTab();
	
	public IncForgeCreativeTab() {
		super(Inc.MODID);
		hideTitle();
		//forge pls. I do what i want
		//noinspection deprecation
		setBackgroundSuffix("incorporeal.png");
	}
	
	@Override
	public ItemStack makeIcon() {
		return new ItemStack(IncItems.CORPOREA_TICKET);
	}
	
	@Override
	public boolean hasSearchBar() {
		//Forge extension
		return true;
	}
}
