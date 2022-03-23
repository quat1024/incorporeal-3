package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.AbstractSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.EnderSoulCoreBlockEntity;
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
import org.jetbrains.annotations.Nullable;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.mixin.client.AccessorModelManager;

import java.util.Map;

public class SoulCoreBlockEntityRenderer<T extends AbstractSoulCoreBlockEntity> implements BlockEntityRenderer<T>, MyDynamicItemRenderer {
	//block entities
	public SoulCoreBlockEntityRenderer(BlockState state, BlockEntityRendererProvider.Context ctx) {
		this(state, ctx.getBlockRenderDispatcher().getBlockModel(state), ctx.getModelSet());
	}
	
	//item stacks
	public SoulCoreBlockEntityRenderer(BlockState state) {
		this(state, Minecraft.getInstance().getBlockRenderer().getBlockModel(state), Minecraft.getInstance().getEntityModels());
	}
	
	public static SoulCoreBlockEntityRenderer<EnderSoulCoreBlockEntity> createBlockless(ResourceLocation modelLocation) {
		ModelManager manager = Minecraft.getInstance().getModelManager();
		Map<ResourceLocation, BakedModel> modelMap = ((AccessorModelManager) manager).getBakedRegistry();
		
		BakedModel model = modelMap.getOrDefault(modelLocation, manager.getMissingModel());
		
		return new SoulCoreBlockEntityRenderer<>(
			IncBlocks.ENDER_SOUL_CORE.defaultBlockState(), //i guess
			model,
			Minecraft.getInstance().getEntityModels()
		);
	}
	
	//common denominator
	public SoulCoreBlockEntityRenderer(BlockState state, BakedModel model, EntityModelSet entityModelSet) {
		this.state = state;
		this.model = model;
		this.playerSkullModel = new SkullModel(entityModelSet.bakeLayer(ModelLayers.PLAYER_HEAD));
	}
	
	private final BlockState state;
	private final BakedModel model;
	private final SkullModelBase playerSkullModel;
	
	//BlockEntityRenderer
	@Override
	public void render(@Nullable AbstractSoulCoreBlockEntity core, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
		int hash = core == null ? 0 : Mth.murmurHash3Mixer(core.getBlockPos().hashCode()) & 0xFFFF;
		float ticks = ClientTickHandler.total();
		
		pose.pushPose();
		
		//Without this, the item renderer looks a TINY bit off centered for some reason
		//No idea why. Might just be me lol
		pose.translate(core == null ? .52 : .5, .5, .5);
		
		initialWobble(pose, hash, ticks);
		
		if(core != null) {
			if(core.hasOwnerProfile()) {
				pose.pushPose();
				
				pose.scale(14 / 16f, 14 / 16f, 14 / 16f);
				wobbleSkull(pose, hash, ticks);
				VertexConsumer buffer = bufs.getBuffer(SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, core.getOwnerProfile()));
				playerSkullModel.renderToBuffer(pose, buffer, light, overlay, 1f, 1f, 1f, 1f);
				
				pose.popPose();
			}
		}
		
		wobbleCubes(pose, hash, ticks);
		
		pose.translate(-.5, -.5, -.5); //blockmodels render from their corner, the old shit rendered from the center

		VertexConsumer buffer = bufs.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
		Minecraft.getInstance().getBlockRenderer().getModelRenderer()
				.renderModel(pose.last(), buffer, state, model, 1f, 1f, 1f, light, overlay);
		
		pose.popPose();
	}
	
	//MyDynamicItemRenderer
	@Override
	public void render(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
		render(null, ClientTickHandler.total(), pose, bufs, light, overlay);
	}
	
	private static void initialWobble(PoseStack pose, int hash, float ticks) {
		pose.mulPose(Vector3f.YP.rotationDegrees((hash + ticks) * 2 % 360));
		pose.translate(0, 0.1 * Inc.sinDegrees((hash + ticks) * 4), 0);
	}
	
	private static void wobbleSkull(PoseStack pose, int hash, float ticks) {
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
	}
	
	private static void wobbleCubes(PoseStack pose, int hash, float ticks) {
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
	}
}
