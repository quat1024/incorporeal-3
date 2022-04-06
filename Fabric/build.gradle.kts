import net.fabricmc.loom.configuration.ide.RunConfigSettings;

plugins {
    //Apply the fabric-conventions plugin declared in buildSrc.
    id("incorporeal.fabric-conventions")
    //Bring a bunch of maven repositories into scope.
    id("incorporeal.repositories")
}

dependencies {
    val fabricApiVersion: String by project
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = fabricApiVersion)
    
    val botaniaVersion: String by project
    val botania = modImplementation(group = "vazkii.botania", name = "Botania", version = williePls(botaniaVersion))
    
    //If requested, declare Botania as a "changing" dependency.
    //This makes Gradle cache it for a much shorter period of time.
    //See java-conventions for where this is configured.
    val declareBotaniaAsChanging: String by project
    if(declareBotaniaAsChanging == "true") {
        botania.isChanging = true
    }
    
    //botania-fabric's transitive dependencies as of Mar 23, 2022; https://github.com/VazkiiMods/Botania/blob/901045768a3637c8dd64a929837ec12672a11f5a/Fabric/build.gradle
    //Botania does not (currently) publish transitive dependency information for its artifacts.
    //See the bottom of java-conventions for a possible reason why.
    
    //Actually it currently currently DOES publish transitive deps, but only on botania-fabric and not anything else.
    //What? Okay. I'll leave them commented out for the time being.
    
//    modImplementation(group = "vazkii.patchouli"                             , name = "Patchouli"                   , version = "1.18.2-66-FABRIC-SNAPSHOT")
//    modImplementation(group = "me.zeroeightsix"                              , name = "fiber"                       , version = "0.23.0-2")
//    modImplementation(group = "io.github.onyxstudios.Cardinal-Components-API", name = "cardinal-components-base"    , version = "4.0.1")
//    modImplementation(group = "io.github.onyxstudios.Cardinal-Components-API", name = "cardinal-components-entity"  , version = "4.0.1")
//    modImplementation(group = "dev.emi"                                      , name = "trinkets"                    , version = "3.3.0") {
//        isTransitive = false
//    }
//    modImplementation(group = "com.jamieswhiteshirt"                         , name = "reach-entity-attributes"     , version = "2.1.1")
//    modImplementation(group = "com.github.emilyploszaj"                      , name = "step-height-entity-attribute", version = "v1.0.1")
}

//Ok so botnio forge and fabric are published under the same maven group, but the fabric one has a differently formatted version string
//1.18.1-428 -> 1.18.1-428-FABRIC
//1.18.1-429-SNAPSHOT -> 1.18.1-429-FABRIC-SNAPSHOT
fun williePls(input: String): String {
    val parts = input.split('-').toMutableList()
    parts.add(2, "FABRIC")
    return parts.joinToString(separator = "-")
}

loom {
    runs {
        create("common-datagen") {
            configureDatagenRunConfig(this, file("../Common/src/generated/resources"), "common")
        }

        create("fabric-datagen") {
            configureDatagenRunConfig(this, file("../Fabric/src/generated/resources"), "fabric")
        }

        create("forge-datagen") {
            configureDatagenRunConfig(this, file("../Forge/src/generated/resources"), "forge")
        }
    }
}

fun configureDatagenRunConfig(settings: RunConfigSettings, outDir: File, identifier: String) {
    //Loom doesn't generate run configs by default in subprojects.
    settings.ideConfigGenerated(true);

    //Base it off the client run config.
    settings.client()
    settings.runDir("./run/datagen_work")
    
    //Configure fabric api datagen's settings.
    settings.vmArg("-Dfabric-api.datagen")
    settings.vmArg("-Dfabric-api.datagen.modid=incorporeal")
    settings.vmArg("-Dfabric-api.datagen.output-dir=${outDir}")
    
    //Tell FabricDatagenEntrypoint which arm I'm generating resources for.
    settings.vmArg("-Dincorporeal.datagen.which=${identifier}")
    
    //Tell EnUsRewriter where en_us.json is. This is used to permit automatic generation of language keys.
    settings.vmArg("-Dincorporeal.en-us=${file("../Common/src/main/resources/assets/incorporeal/lang/en_us.json")}")
}