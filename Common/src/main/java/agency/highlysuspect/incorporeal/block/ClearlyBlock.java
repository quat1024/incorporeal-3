package agency.highlysuspect.incorporeal.block;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Clearly you do not own an air fryer
 */
public class ClearlyBlock extends Block {
	public ClearlyBlock(Properties $$0) {
		super($$0);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag piss) {
		super.appendHoverText(stack, level, tooltip, piss);
		tooltip.add(new TranslatableComponent("block.incorporeal.clearly.fryer"));
	}
}
