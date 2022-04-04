package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.block.entity.UnstableCubeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.client.core.handler.ClientTickHandler;

/**
 * The BlockEntityRenderer for the Unstable Cube. In charge of rotating at a weird angle, tinting it, etc.
 */
public abstract class UnstableCubeRenderers {
	public static BlockEntityRenderer<UnstableCubeBlockEntity> createBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		return new BlockEntity();
	}
	
	public static MyDynamicItemRenderer createItemRenderer(DyeColor color) {
		return new Item(color);
	}
	
	protected void drawCube(BlockState state, BakedModel model, PoseStack pose, MultiBufferSource bufs, int light, int overlay, float red, float green, float blue) {
		//ty artemis for this! i changed it tbh
		VertexConsumer buffer = bufs.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(pose.last(), buffer, state, model, red, green, blue, light, overlay);
	}
	
	private static final Vector3f XZP;
	static {
		Vector3f xp = Vector3f.XP.copy();
		xp.add(Vector3f.ZP);
		xp.normalize();
		XZP = xp;
	}
	
	//"hash" is a random, somewhat-small number unique per placed block.
	//specifying "somewhat small" because if it's too big, you can get into float farlands
	protected static void roll(PoseStack pose, float partialTicks, float angle, float speed, float bump, float bumpDecay, int hash, float bonusExtraScaling) {
		float ticks = ClientTickHandler.ticksInGame + partialTicks;
		
		//angle/speed/ticks are mathed out 20 times a second only (in client BE ticker)
		//crappily interpolate
		float predictedAngle = angle + (speed * partialTicks);
		float predictedBump = bump * Inc.rangeRemap(partialTicks, 0, 1, bump, bump * bumpDecay);
		int flip = (hash & 1) == 0 ? -1 : 1;
		
		//base spin
		pose.translate(.5, .5, .5);
		pose.mulPose(Vector3f.YP.rotationDegrees((flip * predictedAngle + hash) % 360));
		
		//wobble (don't ask how this works)
		float wobble = ticks + hash;
		float wobbleSin = Inc.sinDegrees(wobble);
		float wobbleCos = Inc.cosDegrees(wobble);
		float wobbleAmountDegrees = 15 * flip;
		pose.mulPose(XZP.rotationDegrees(Mth.sin(hash + ticks * 0.02f) * 40 * flip));
		pose.mulPose(Vector3f.XP.rotationDegrees(wobbleCos * wobbleAmountDegrees));
		pose.mulPose(Vector3f.XP.rotationDegrees(wobbleSin * wobbleAmountDegrees));
		pose.mulPose(Vector3f.ZP.rotationDegrees(-wobbleSin * wobbleAmountDegrees));
		pose.mulPose(Vector3f.ZP.rotationDegrees(-wobbleCos * wobbleAmountDegrees));
		
		//bump; scales the cube up if it was struck recently
		float upscale = (predictedBump * 0.7f) + 1 + bonusExtraScaling;
		pose.scale(upscale, upscale, upscale);
		
		//as with any good PoseStack scheme, there's some random fixup you have to do at the end
		pose.translate(-.5, -.5, -.5);
	}
	
	//These classes are kept private mainly to avoid egregious name-clashing with the intellij auto-import feature lol
	//Ctors available up above
	
	private static class BlockEntity extends UnstableCubeRenderers implements BlockEntityRenderer<UnstableCubeBlockEntity> {
		@Override
		public void render(@NotNull UnstableCubeBlockEntity cube, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
			pose.pushPose();
			
			BlockState state = cube.getBlockState();
			BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
			
			UnstableCubeBlock block = (UnstableCubeBlock) state.getBlock();
			int colorPacked = block.color.getFireworkColor();
			float red = ((colorPacked & 0xFF0000) >> 16) / 255f;
			float green = ((colorPacked & 0x00FF00) >> 8) / 255f;
			float blue = (colorPacked & 0x0000FF) / 255f;
			
			int hash = Mth.murmurHash3Mixer(cube.getBlockPos().hashCode()) & 0xFFFF;
			roll(pose, partialTicks, cube.angle, cube.speed, cube.bump, cube.bumpDecay, hash, 0);
			
			drawCube(state, model, pose, bufs, light, overlay, red, green, blue);
			pose.popPose();
		}
	}
	
	private static class Item extends UnstableCubeRenderers implements MyDynamicItemRenderer {
		public Item(DyeColor color) {
			this.state = IncBlocks.UNSTABLE_CUBES.get(color).defaultBlockState();
			this.model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
			
			//Just for fun;)
			//This did...... not turn out to offset all cubes by only 2.5 degrees, but idk it looks cool either way
			this.rotationOffset = (float) Math.toDegrees(color.ordinal() * 2.5f);
			
			int colorPacked = color.getFireworkColor();
			this.red = ((colorPacked & 0xFF0000) >> 16) / 255f;
			this.green = ((colorPacked & 0x00FF00) >> 8) / 255f;
			this.blue = (colorPacked & 0x0000FF) / 255f;
		}
		
		private final BlockState state;
		private final BakedModel model;
		private final float rotationOffset;
		private final float red, green, blue;
		
		@Override
		public void render(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
			pose.pushPose();
			
			roll(pose, ClientTickHandler.total(), rotationOffset, 0, 0, 0, 0, 0.4f);
			drawCube(state, model, pose, bufs, light, overlay, red, green, blue);
			
			pose.popPose();
		}
	}
}
