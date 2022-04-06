package agency.highlysuspect.incorporeal.platform.fabric.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.datagen.DataDsl;
import agency.highlysuspect.incorporeal.datagen.IncCommonBlockLootGen;
import agency.highlysuspect.incorporeal.datagen.IncCommonModelsAndBlockstates;
import agency.highlysuspect.incorporeal.datagen.IncCommonRecipeGen;
import agency.highlysuspect.incorporeal.datagen.IncCommonTagGen;
import agency.highlysuspect.incorporeal.datagen.IncCommonLexiconGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FabricDatagenEntrypoint implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		Inc.LOGGER.info("datagen output folder: {}", gen.getOutputFolder().toAbsolutePath());
		
		String which = System.getProperty("incorporeal.datagen.which", "common");
		Inc.LOGGER.info("which: {}", which);
		
		//DUDE its so NICE having switch expressions that don't fallthrough!! 
		switch(which) {
			case "common" -> {
				DataDsl.addProvider(gen, "Incorporeal block loot tables", IncCommonBlockLootGen::doIt);
				DataDsl.addProvider(gen, "Incorporeal block and item tags", IncCommonTagGen::doIt);
				DataDsl.addProvider(gen, "Incorporeal recipes", IncCommonRecipeGen::doIt);
				DataDsl.addProvider(gen, "Incorporeal Lexica Botania entries", IncCommonLexiconGen::doIt);
				DataDsl.addProvider(gen, "Incorporeal blockstates and block models", IncCommonModelsAndBlockstates::doIt);
			}
			case "fabric" -> DataDsl.addProvider(gen, "Incorporeal block and item tags (for Fabric)", IncFabricTagGen::doIt);
			case "forge" -> DataDsl.addProvider(gen, "Incorporeal block and item tags (for Forge)", IncForgeTagGen::doIt);
		}
	}
}
