package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.datagen.IncCommonDatagen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class IncFabricDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		System.out.println("datagen output folder: " + gen.getOutputFolder().toAbsolutePath());
		
		//todo: fabric specific datagen eventually
		IncCommonDatagen.configureCommonDatagen(gen);
	}
}
