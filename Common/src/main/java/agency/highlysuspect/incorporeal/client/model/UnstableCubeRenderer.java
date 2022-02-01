package agency.highlysuspect.incorporeal.client.model;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.entity.UnstableCubeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.client.core.handler.ClientTickHandler;

public class UnstableCubeRenderer implements BlockEntityRenderer<UnstableCubeBlockEntity> {
	public UnstableCubeRenderer(DyeColor color, BlockEntityRendererProvider.Context ctx) {
		model = new UnstableCubeModel(ctx.bakeLayer(IncModelLayers.UNSTABLE_CUBE));
		
		int colorPacked = color.getFireworkColor();
		red = ((colorPacked & 0xFF0000) >> 16) / 255f;
		green = ((colorPacked & 0x00FF00) >> 8) / 255f;
		blue = (colorPacked & 0x0000FF) / 255f;
	}
	
	private static final ResourceLocation texture = Inc.id("textures/entity/unstable_cube.png");
	private final UnstableCubeModel model;
	private final float red, green, blue;
	
	@Override
	public void render(@Nullable UnstableCubeBlockEntity cube, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
		pose.pushPose();
		
		if(cube == null) {
			roll(pose, partialTicks, 0, 0, 0, 0, 0);
		} else {
			int hash = Mth.murmurHash3Mixer(cube.getBlockPos().hashCode()) & 0xFFFF;
			roll(pose, partialTicks, cube.angle, cube.speed, cube.bump, cube.bumpDecay, hash);
		}
		
		VertexConsumer buffer = bufs.getBuffer(model.renderType(texture));
		model.renderToBuffer(pose, buffer, light, overlay, red, green, blue, 1);
		
		pose.popPose();
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
	public static void roll(PoseStack pose, float partialTicks, float angle, float speed, float bump, float bumpDecay, int hash) {
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
		float upscale = (predictedBump * 0.7f) + 1;
		pose.scale(upscale, upscale, upscale);
		
		//as with any good PoseStack scheme, there's some random fixup you have to do at the end
		pose.scale(.5f, .5f, .5f);
		pose.translate(-.5, -.5, -.5);
	}
}
