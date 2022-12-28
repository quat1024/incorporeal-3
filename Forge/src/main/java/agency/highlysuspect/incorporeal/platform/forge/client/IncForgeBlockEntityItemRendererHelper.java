package agency.highlysuspect.incorporeal.platform.forge.client;

import agency.highlysuspect.incorporeal.client.IncClientProperties;
import agency.highlysuspect.incorporeal.client.MyDynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Mostly a copy paste of ForgeBlockEntityItemRendererHelper from botania.
 * 
 * TODO: Dump the cache on resource reload? (then PR it to botania)
 */
public class IncForgeBlockEntityItemRendererHelper {
	private static final Map<Item, MyDynamicItemRenderer> rendererCache = new IdentityHashMap<>();
	private static @Nullable MyDynamicItemRenderer findRenderer(Item item) {
		return rendererCache.computeIfAbsent(item, __ -> {
			Supplier<MyDynamicItemRenderer> my = IncClientProperties.MY_DYNAMIC_ITEM_RENDERERS.get(item);
			if(my == null) return null;
			else return my.get();
		});
	}
	
	// Nulls in ctor call are fine, we don't use those fields
	private static final BlockEntityWithoutLevelRenderer RENDERER = new BlockEntityWithoutLevelRenderer(null, null) {
		@Override
		public void renderByItem(ItemStack stack, ItemTransforms.TransformType transform, PoseStack ps, MultiBufferSource buffers, int light, int overlay) {
			MyDynamicItemRenderer renderer = findRenderer(stack.getItem());
			if(renderer != null) renderer.render(stack, transform, ps, buffers, light, overlay);
		}
	};
	
	public static final IClientItemExtensions PROPS = new IClientItemExtensions() {
		@Override
		public BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return IncForgeBlockEntityItemRendererHelper.RENDERER;
		}
	};
}
