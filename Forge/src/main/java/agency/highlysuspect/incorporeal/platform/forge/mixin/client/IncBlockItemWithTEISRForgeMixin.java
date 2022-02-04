package agency.highlysuspect.incorporeal.platform.forge.mixin.client;

import agency.highlysuspect.incorporeal.item.IncBlockItemWithTEISR;
import agency.highlysuspect.incorporeal.platform.forge.client.IncForgeBlockEntityItemRendererHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

//self-mixin
@Mixin(IncBlockItemWithTEISR.class)
public class IncBlockItemWithTEISRForgeMixin extends Item {
	//Dummy constructor for mixin
	public IncBlockItemWithTEISRForgeMixin(Properties dummy) {
		super(dummy);
	}
	
	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		IncForgeBlockEntityItemRendererHelper.initItem(consumer);
	}
}
