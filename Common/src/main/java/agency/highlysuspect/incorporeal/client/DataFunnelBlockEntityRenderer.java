package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.entity.DataFunnelBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.helper.MathHelper;

import java.util.Set;

public class DataFunnelBlockEntityRenderer implements BlockEntityRenderer<DataFunnelBlockEntity> {
	public DataFunnelBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		funnelModel = ctx.getBlockRenderDispatcher().getBlockModel(IncBlocks.DATA_FUNNEL.defaultBlockState());
	}
	
	private final BakedModel funnelModel;
	
	@Override
	public void render(DataFunnelBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, int overlay) {
		int hash = Mth.murmurHash3Mixer(be.getBlockPos().hashCode()) & 0xFFFF;
		
		pose.pushPose();
		pose.translate(.5, .5, .5);
		
		Set<BlockPos> bindings = be.getBindings();
		float[] angles;
		
		if(bindings.isEmpty()) {
			angles = new float[2];
			angles[0] = ClientTickHandler.total() * 2.1f; //yaw
			angles[1] = Inc.sinDegrees(ClientTickHandler.total() * 2) * 20; //pitch
			angles[0] %= 360;
			angles[1] %= 360;
		} else {
			angles = new float[bindings.size() * 2];
			
			//Generate euler angles to "look at" the bind target
			//Yeah man this is crusty!!! i need a better background in computer graphics
			Vec3 here = Vec3.atCenterOf(be.getBlockPos());
			int index = 0;
			for(BlockPos binding : bindings) {
				Vec3 there = Vec3.atCenterOf(binding);
				
				float planarDistance = MathHelper.pointDistancePlane(here.x, here.z, there.z, there.z);
				float heightDifference = (float) (there.y - here.y);
				if(planarDistance < 0.0001) {
					angles[index] = 0; //yaw
					angles[index + 1] = heightDifference > 0 ? 180 : -180; //pitch
				} else {
					angles[index] = (float) Math.toDegrees(Math.atan2(here.x - there.x, here.z - there.z)); //yaw
					//pitch
					if(Math.abs(heightDifference) < 0.0001) {
						angles[index + 1] = 0;
					} else {
						//angles[index + 1] = (float) Math.toDegrees(Math.tan(heightDifference / planarDistance)); //doesn't work??
						//pasted out of spreader code, fucking sick of this lol
						Vec3 diffVec = there.subtract(here);
						Vec3 rotVec = new Vec3(diffVec.x, 0, diffVec.z);
						float angle = (float) (MathHelper.angleBetween(diffVec, rotVec) * 180F / Math.PI);
						if (there.y < here.y) {
							angle = -angle;
						}
						angles[index + 1] = angle;
					}
				}
				
				index += 2;
			}
		}
		
		VertexConsumer buffer = bufs.getBuffer(ItemBlockRenderTypes.getRenderType(be.getBlockState(), false));
		ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
		
		for(int i = 0; i < angles.length; i += 2) {
			float yaw = angles[i];
			float pitch = angles[i + 1];
			
			pose.pushPose();
			
			//apply pitch and yaw (euler angle) rotations
			Quaternion transform = Vector3f.YP.rotationDegrees(yaw);
			transform.mul(Vector3f.XP.rotationDegrees(pitch));
			pose.mulPose(transform);
			
			//pulse
			float wow = Inc.rangeRemap(Inc.sinDegrees(ClientTickHandler.total() * 4 + (i * 27) + hash), -1, 1, 0.9f, 1.2f);
			pose.scale(wow, wow, wow);
			
			//draw the cube. (it's in the center of the blockmodel, that's why the translation)
			pose.translate(-.5, -.5, -.5);
			renderer.renderModel(pose.last(), buffer, be.getBlockState(), funnelModel, 1, 1, 1, light, overlay);
			
			pose.popPose();
		}
		
		pose.popPose();
	}
}
