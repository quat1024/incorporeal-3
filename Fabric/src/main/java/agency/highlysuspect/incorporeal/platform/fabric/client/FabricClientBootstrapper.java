package agency.highlysuspect.incorporeal.platform.fabric.client;

import agency.highlysuspect.incorporeal.client.IncClientNetwork;
import agency.highlysuspect.incorporeal.client.IncClientProperties;
import agency.highlysuspect.incorporeal.client.MyDynamicItemRenderer;
import agency.highlysuspect.incorporeal.platform.IncClientBootstrapper;
import agency.highlysuspect.incorporeal.platform.fabric.IncBootstrapFabric;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaFabricClientCapabilities;

import java.util.function.Supplier;

public class FabricClientBootstrapper implements IncClientBootstrapper {
	@Override
	public void registerItemPropertyOverrides() {
		IncClientProperties.registerPropertyOverrides((item, id, prop) -> {
			ClampedItemPropertyFunction clamped;
			if(prop instanceof ClampedItemPropertyFunction c) clamped = c;
			else clamped = new ClampedItemPropertyFunction() {
				//A ClampedItemPropertyFunction that delegates straight through, without actually clamping.
				//This is needed due to a mistake in Fabric API: https://github.com/FabricMC/fabric/issues/2107
				@Override
				public float call(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
					return prop.call(itemStack, clientLevel, livingEntity, i);
				}
				
				@Override
				public float unclampedCall(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
					return prop.call(itemStack, clientLevel, livingEntity, i);
				}
			};
			
			FabricModelPredicateProviderRegistry.register(item.asItem(), id, clamped);
		});
	}
	
	@Override
	public void registerBlockRenderLayers() {
		IncClientProperties.registerRenderTypes(BlockRenderLayerMap.INSTANCE::putBlock);
	}
	
	@Override
	public void registerColorProviders() {
		IncClientProperties.registerBlockColorProviders(ColorProviderRegistry.BLOCK::register);
		IncClientProperties.registerItemColorProviders(ColorProviderRegistry.ITEM::register);
	}
	
	@Override
	public void registerExtraModelsToBake() {
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> IncClientProperties.registerExtraModelsToBake(out));
	}
	
	@Override
	public void registerBlockEntityRenderers() {
		IncClientProperties.registerBlockEntityRenderers(BlockEntityRendererRegistry::register);
	}
	
	@Override
	public void registerEntityRenderers() {
		IncClientProperties.registerEntityRenderers(EntityRendererRegistry::register);
	}
	
	@SuppressWarnings("ClassCanBeRecord") //Invariant in constructor must be maintained
	private static class LazilyAccessedDynamicItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
		public LazilyAccessedDynamicItemRenderer(Supplier<MyDynamicItemRenderer> wow) {
			this.wow = Suppliers.memoize(wow::get);
		}
		
		private final Supplier<MyDynamicItemRenderer> wow;
		
		@Override
		public void render(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
			wow.get().render(stack, mode, matrices, vertexConsumers, light, overlay);
		}
	}
	
	@Override
	public void registerItemRenderers() {
		IncClientProperties.MY_DYNAMIC_ITEM_RENDERERS.forEach((item, rendererSupplier) ->
			BuiltinItemRendererRegistry.INSTANCE.register(item, new LazilyAccessedDynamicItemRenderer(rendererSupplier)));
	}
	
	@Override
	public void registerClientCapabilities() {
		IncClientProperties.WAND_HUD_MAKERS.forEach((type, maker) ->
			BotaniaFabricClientCapabilities.WAND_HUD.registerForBlockEntities((be, context) -> maker.apply(be), type));
	}
	
	@Override
	public void registerServerToClientNetworkChannelReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(IncBootstrapFabric.NETWORK_ID, (client, handler, buf, responseSender) -> IncClientNetwork.handle(buf));
	}
}
