plugins {
    //Apply the fabric-conventions plugin declared in buildSrc.
    id("incorporeal.fabric-conventions")
    //I'm keeping all the repositories {} in this file to reduce clutter.
    id("incorporeal.repositories")
}

dependencies {
    //Fabric API, the whole thing (maven.fabricmc.net)
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.44.0+1.18")
    
    //botania-fabric (maven.blamejared.com)
    modImplementation(group = "vazkii.botania", name = "Botania", version = "1.18.1-428-FABRIC-SNAPSHOT")
    
    //botania-fabric's transitive dependencies.
    //Due to reasons, Botania does not (currently) publish transitive dependency information for its artifacts.
    //Transitive dependencies sourced on Jan 29, 2022 from https://github.com/VazkiiMods/Botania/blob/1b16b5672fd7c59f4fa0e5e235f9b8120b84dcb2/Fabric/build.gradle
    //All 'include' dependencies were removed, I don't need to re-include them in this mod.
    
    //Patchouli (maven.blamejared.com)
    modImplementation(group = "vazkii.patchouli", name = "Patchouli", version = "1.18.1-64-FABRIC")
    //Fiber (maven.modmuss50.me)
    modImplementation(group = "me.zeroeightsix", name = "fiber", version = "0.23.0-2")
    //Cardinal Components API, some of it (ladysnake.jfrog.io/artifactory/mods)
    modImplementation(group = "io.github.onyxstudios.Cardinal-Components-API", name = "cardinal-components-base", version = "4.0.1")
    modImplementation(group = "io.github.onyxstudios.Cardinal-Components-API", name = "cardinal-components-entity", version = "4.0.1")
    //Trinkets (maven.terraformersmc.com)
    modImplementation(group = "dev.emi", name = "trinkets", version = "3.1.0")
    //Reach Entity Attributes (https://maven.jamieswhiteshirt.com/libs-release/)
    modImplementation(group = "com.jamieswhiteshirt", name = "reach-entity-attributes", version = "2.1.1")
    //Step Height Entity Attribute (jitpack :V )
    modImplementation(group = "com.github.emilyploszaj", name = "step-height-entity-attribute", version = "v1.0.1")
}