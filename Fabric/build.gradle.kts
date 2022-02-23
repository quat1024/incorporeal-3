plugins {
    //Apply the fabric-conventions plugin declared in buildSrc.
    id("incorporeal.fabric-conventions")
    //Bring a bunch of maven repositories into scope.
    id("incorporeal.repositories")
}

dependencies {
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.44.0+1.18")
    modImplementation(group = "vazkii.botania"         , name = "Botania"   , version = "1.18.1-428-FABRIC-SNAPSHOT").isChanging = true
    
    //botania-fabric's transitive dependencies as of Jan 29, 2022; https://github.com/VazkiiMods/Botania/blob/1b16b5672fd7c59f4fa0e5e235f9b8120b84dcb2/Fabric/build.gradle
    //Due to reasons, Botania does not (currently) publish transitive dependency information for its artifacts.
    modImplementation(group = "vazkii.patchouli"                             , name = "Patchouli"                   , version = "1.18.1-64-FABRIC")
    modImplementation(group = "me.zeroeightsix"                              , name = "fiber"                       , version = "0.23.0-2")
    modImplementation(group = "io.github.onyxstudios.Cardinal-Components-API", name = "cardinal-components-base"    , version = "4.0.1")
    modImplementation(group = "io.github.onyxstudios.Cardinal-Components-API", name = "cardinal-components-entity"  , version = "4.0.1")
    modImplementation(group = "dev.emi"                                      , name = "trinkets"                    , version = "3.1.0")
    modImplementation(group = "com.jamieswhiteshirt"                         , name = "reach-entity-attributes"     , version = "2.1.1")
    modImplementation(group = "com.github.emilyploszaj"                      , name = "step-height-entity-attribute", version = "v1.0.1")
}

loom {
    runs {
        //Common Datagen. One loader has to be the one in charge of it, and it'll be fabric.
        create("common-datagen") {
            client()
            vmArg("-Dbotania.xplat_datagen=1")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.modid=incorporeal")
            //Doesn't work lol
            //vmArg("-Dfabric-api.datagen.output-dir=${project(":Common").relativePath("./src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.output-dir=${file("../Common/src/generated/resources")}")
            
            //for EnUsRewriter:
            vmArg("-Dincorporeal.en-us=${file("../Common/src/main/resources/assets/incorporeal/lang/en_us.json")}")
        }

        //Loom doesn't generate run configs by default in subprojects.
        configureEach {
            runDir("./run/datagen_work")
            ideConfigGenerated(true)
        }
    }
}