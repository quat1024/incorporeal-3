package agency.highlysuspect.incorporeal.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * I'm not exactly sure what LayerDefinitions are all about. It feels like a half-finished "json entity model" system.
 */
public class IncClientLayerDefinitions {
	public static void register(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> r) {
		r.accept(IncClientModelLayers.UNSTABLE_CUBE, () -> LayerDefinition.create(UnstableCubeModel.createMesh(), 32, 16));
	}
}
