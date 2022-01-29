plugins {
    //Apply the fabric-conventions plugin declared in buildSrc.
    id("incorporeal.fabric-conventions")
    //Bring a bunch of maven repositories into scope.
    id("incorporeal.repositories")
}

dependencies {
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.44.0+1.18")
    modImplementation(group = "vazkii.botania"         , name = "Botania"   , version = "1.18.1-428-FABRIC-SNAPSHOT")
    
    //Hack to get this subproject to compile. It doesn't run atm.
    //Basically botania-fabric-snapshot is a little outdated compared to botania-xplat, due to a jenkins issue.
    //This whole setup is about writing code in :Common against -xplat, but compiling it here against :Fabric,
    //so understandably one of these artifacts getting outdated will cause an issue.
    compileOnly(group = "vazkii.botania", name = "Botania-xplat", version = "1.18.1-428-SNAPSHOT")
    
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