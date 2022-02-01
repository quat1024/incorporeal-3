package agency.highlysuspect.incorporeal.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class UnstableCubeModel extends Model {
	public UnstableCubeModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.cube = root.getChild("cube");
	}
	
	private final ModelPart cube;
	
	public static MeshDefinition createMesh() {
		//No idea what this is all about, but 99% sure it means mojang is working on json entity models lol
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		
		part.addOrReplaceChild("cube",
			CubeListBuilder.create().addBox(-8, -8, -8, 16, 16, 16),
			PartPose.offset(8, 8, 8));
		
		return mesh;
	}
	
	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
		cube.render(ms, buffer, light, overlay, r, g, b, a);
	}
}
