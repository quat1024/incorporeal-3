package agency.highlysuspect.incorporeal.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class IncModelDefinitions {
	public static void register(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> r) {
		r.accept(IncModelLayers.UNSTABLE_CUBE, () -> LayerDefinition.create(UnstableCubeModel.createMesh(), 32, 16));
	}
}
