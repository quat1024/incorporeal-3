package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.UnstableCubeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.client.core.handler.ClientTickHandler;

/**
 * The BlockEntityRenderer for the Unstable Cube. In charge of rotating at a weird angle, tinting it, etc.
 * Note that it takes an additional DyeColor parameter, for the color of the cube to draw - Botania's TEISR
 * machinery passes a `null` BlockEntity in the item renderer, but I still need to know what color to make
 * the unstable cube model when rendering an item.
 */
public class UnstableCubeBlockEntityRenderer implements BlockEntityRenderer<UnstableCubeBlockEntity>, MyDynamicItemRenderer {
	//block entities
	public UnstableCubeBlockEntityRenderer(DyeColor color, BlockEntityRendererProvider.Context ctx) {
		this(color, ctx.getBlockRenderDispatcher());
	}
	
	//item stacks
	public UnstableCubeBlockEntityRenderer(DyeColor color) {
		this(color, Minecraft.getInstance().getBlockRenderer());
	}
	
	//common denominator
	private UnstableCubeBlockEntityRenderer(DyeColor color, BlockRenderDispatcher dispatcher) {
		this.state = IncBlocks.UNSTABLE_CUBES.get(color).defaultBlockState();
		this.cubeModel = dispatcher.getBlockModel(state);
		
		int colorPacked = color.getFireworkColor();
		this.red = ((colorPacked & 0xFF0000) >> 16) / 255f;
		this.green = ((colorPacked & 0x00FF00) >> 8) / 255f;
		this.blue = (colorPacked & 0x0000FF) / 255f;
	}
	
	private final BakedModel cubeModel;
	private final BlockState state;
	private final float red, green, blue;
	
	//BlockEntityRenderer
	@Override
	public void render(@Nullable UnstableCubeBlockEntity cube, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
		pose.pushPose();
		
		if(cube != null) {
			int hash = Mth.murmurHash3Mixer(cube.getBlockPos().hashCode()) & 0xFFFF;
			roll(pose, partialTicks, cube.angle, cube.speed, cube.bump, cube.bumpDecay, hash, 0);
		} else { //Item renderer
			roll(pose, partialTicks, 0, 0, 0, 0, 0, 0.4f);
		}
		
		drawCube(pose, bufs, light, overlay);
		pose.popPose();
	}
	
	//MyDynamicItemRenderer
	@Override
	public void render(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
		render(null, ClientTickHandler.total(), pose, bufs, light, overlay);
	}
	
	private void drawCube(PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
		//ty artemis for this! i changed it tbh
		VertexConsumer buffer = bufs.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(pose.last(), buffer, state, cubeModel, red, green, blue, light, overlay);
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
	public static void roll(PoseStack pose, float partialTicks, float angle, float speed, float bump, float bumpDecay, int hash, float bonusExtraScaling) {
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
}
