package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.datagen.DataDsl;
import agency.highlysuspect.incorporeal.datagen.IncCommonBlockLootGen;
import agency.highlysuspect.incorporeal.datagen.IncCommonRecipeGen;
import agency.highlysuspect.incorporeal.datagen.IncCommonTagGen;
import agency.highlysuspect.incorporeal.datagen.IncCommonLexiconGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataGenerator;

public class IncFabricDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		Inc.LOGGER.info("datagen output folder: " + gen.getOutputFolder().toAbsolutePath());
		
		if(System.getProperty("botania.xplat_datagen") != null) {
			configureCommonDatagen(gen);
		} else {
			throw new IllegalStateException("Fabric specific datagen is NYI");
		}
	}
	
	public static void configureCommonDatagen(DataGenerator gen) {
		DataDsl.addProvider(gen, "Incorporeal block loot tables", IncCommonBlockLootGen::doIt);
		DataDsl.addProvider(gen, "Incorporeal block and item tags", IncCommonTagGen::doIt);
		DataDsl.addProvider(gen, "Incorporeal recipes", IncCommonRecipeGen::doIt);
		DataDsl.addProvider(gen, "Incorporeal Lexica Botania entries", IncCommonLexiconGen::doIt);
	}
}
