package agency.highlysuspect.incorporeal.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

/**
 * Shaped after Fabric's BuiltinItemRendererRegistry.DynamicItemRenderer, which is further shaped after vanilla's BlockEntityWithoutLevelRenderer.
 */
public interface MyDynamicItemRenderer {
	void render(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack pose, MultiBufferSource bufs, int light, int overlay);
}
