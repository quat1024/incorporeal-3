package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.AbstractSoulCoreBlockEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.mixin.client.ModelManagerAccessor;

import java.util.Map;

/**
 * Renderers (BlockEntityRenderer and MyDynamicItemRenderer) for the Soul Cores.
 */
public class SoulCoreRenderers {
	public static <T extends AbstractSoulCoreBlockEntity> BlockEntityRenderer<T> createBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		return new BlockEntity<>(ctx);
	}
	
	public static MyDynamicItemRenderer createItemRenderer(BlockState state) {
		return new Item(state, Minecraft.getInstance().getBlockRenderer(), Minecraft.getInstance().getEntityModels());
	}
	
	public static MyDynamicItemRenderer createItemRendererForNamedModel(ResourceLocation modelLocation) {
		return new Item(modelLocation, Minecraft.getInstance().getModelManager(), Minecraft.getInstance().getEntityModels());
	}
	
	///
	
	protected SoulCoreRenderers(EntityModelSet modelSet) {
		this.playerSkullModel = new SkullModel(modelSet.bakeLayer(ModelLayers.PLAYER_HEAD));
	}
	
	protected final SkullModelBase playerSkullModel;
	
	protected void initialWobble(PoseStack pose, int hash, float ticks) {
		pose.mulPose(Vector3f.YP.rotationDegrees((hash + ticks) * 2 % 360));
		pose.translate(0, 0.1 * Inc.sinDegrees((hash + ticks) * 4), 0);
	}
	
	protected void drawSkull(PoseStack pose, int hash, float ticks, MultiBufferSource bufs, int light, int overlay, GameProfile skullProfile) {
		pose.pushPose();
		pose.scale(14 / 16f, 14 / 16f, 14 / 16f); //don't ask
		
		float wobble = (hash + ticks) * 5;
		float wobbleSin = Inc.sinDegrees(wobble);
		float wobbleCos = Inc.cosDegrees(wobble);
		float wobbleAmountDegrees = 10f;
		
		pose.mulPose(Vector3f.XP.rotationDegrees(wobbleCos * wobbleAmountDegrees));
		pose.mulPose(Vector3f.XP.rotationDegrees(wobbleSin * wobbleAmountDegrees));
		pose.mulPose(Vector3f.ZP.rotationDegrees(-wobbleCos * wobbleAmountDegrees));
		pose.mulPose(Vector3f.ZP.rotationDegrees(-wobbleSin * wobbleAmountDegrees));
		
		pose.translate(0, -1/4f, 0);
		pose.scale(-1f, -1f, 1f); //deeply magical
		
		VertexConsumer buffer = bufs.getBuffer(SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, skullProfile));
		playerSkullModel.renderToBuffer(pose, buffer, light, overlay, 1f, 1f, 1f, 1f);
		
		pose.popPose();
	}
	
	protected void drawCubes(PoseStack pose, int hash, float ticks, MultiBufferSource bufs, int light, int overlay, BlockState state, BakedModel model) {
		pose.pushPose();
		
		pose.mulPose(Vector3f.YP.rotationDegrees(((-ticks + hash) / 5f) % 360));
		pose.mulPose(Vector3f.YP.rotationDegrees(Mth.sin((ticks + hash) / 50f) * 40));
		
		float wobble2 = (hash + ticks) * 3;
		float wobble2Sin = Inc.sinDegrees(wobble2);
		float wobble2Cos = Inc.cosDegrees(wobble2);
		float wobble2AmountDegrees = 10f;
		pose.mulPose(Vector3f.XP.rotationDegrees(-wobble2Cos * wobble2AmountDegrees));
		pose.mulPose(Vector3f.XP.rotationDegrees(-wobble2Sin * wobble2AmountDegrees));
		pose.mulPose(Vector3f.ZP.rotationDegrees(wobble2Cos * wobble2AmountDegrees));
		pose.mulPose(Vector3f.ZP.rotationDegrees(wobble2Sin * wobble2AmountDegrees));
		
		pose.translate(-.5, -.5, -.5); //blockmodels render from their corner, the old shit rendered from the center
		
		VertexConsumer buffer = bufs.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
		Minecraft.getInstance().getBlockRenderer().getModelRenderer()
			.renderModel(pose.last(), buffer, state, model, 1f, 1f, 1f, light, overlay);
		
		pose.popPose();
	}
	
	///
	
	private static class BlockEntity<T extends AbstractSoulCoreBlockEntity> extends SoulCoreRenderers implements BlockEntityRenderer<T> {
		public BlockEntity(BlockEntityRendererProvider.Context ctx) {
			super(ctx.getModelSet());
		}
		
		@Override
		public void render(@NotNull T core, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
			int hash = Mth.murmurHash3Mixer(core.getBlockPos().hashCode()) & 0xFFFF;
			float ticks = ClientTickHandler.total();
			
			BlockState state = core.getBlockState();
			BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
			
			pose.pushPose();
			
			pose.translate(.5, .5, .5);
			initialWobble(pose, hash, ticks);
			
			if(core.hasOwnerProfile()) {
				drawSkull(pose, hash, ticks, bufs, light, overlay, core.getOwnerProfile());
			}
			
			drawCubes(pose, hash, ticks, bufs, light, overlay, state, model);
			
			pose.popPose();
		}
	}
	
	private static class Item extends SoulCoreRenderers implements MyDynamicItemRenderer {
		public Item(BlockState state, BlockRenderDispatcher dispatcher, EntityModelSet modelSet) {
			super(modelSet);
			
			this.state = state;
			this.model = dispatcher.getBlockModel(state);
		}
		
		//This constructor doesn't take a BlockState. Instead, it looks up a BlockModel by name.
		//Someone must have baked this model at some point. See usages on IncClientProperties.registerExtraModelsToBake.
		public Item(ResourceLocation modelLocation, ModelManager modelManager, EntityModelSet modelSet) {
			super(modelSet);
			
			Map<ResourceLocation, BakedModel> modelMap = ((ModelManagerAccessor) modelManager).getBakedRegistry();
			this.model = modelMap.getOrDefault(modelLocation, modelManager.getMissingModel());
			
			//Minecraft rendering machinery still requires a BlockState to be provided to draw any blockmodel, so idk, i just picked one
			this.state = IncBlocks.ENDER_SOUL_CORE.defaultBlockState();
		}
		
		private final BlockState state;
		private final BakedModel model;
		
		@Override
		public void render(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
			float ticks = ClientTickHandler.total();
			
			pose.pushPose();
			
			//An extra 0.02 units of translation is needed on item renderers to make them look centered, for some reason.
			//I haven't tracked this down.
			pose.translate(.52, .5, .5);
			initialWobble(pose, 0, ticks);
			
			drawCubes(pose, 0, ticks, bufs, light, overlay, state, model);
			
			pose.popPose();
		}
	}
}
