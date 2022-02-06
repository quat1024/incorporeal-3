plugins {
    //Apply the forge-conventions plugin declared in buildSrc.
    id("incorporeal.forge-conventions")
    //Bring a bunch of maven repositories into scope.
    id("incorporeal.repositories")
}

dependencies {
    val botania = implementation(fgDeobf(group = "vazkii.botania", name = "Botania", version = "1.18.1-428-SNAPSHOT"))
    if(botania is ExternalModuleDependency) botania.isChanging = true
    
    //botania-forge's transitive dependencies as of Jan 29, 2022; https://github.com/VazkiiMods/Botania/blob/1b16b5672fd7c59f4fa0e5e235f9b8120b84dcb2/Forge/build.gradle
    //Due to reasons, Botania does not (currently) publish transitive dependency information for its artifacts.
    implementation(fgDeobf(group = "vazkii.patchouli"        , name = "Patchouli"   , version = "1.18.1-64"                         ))
    compileOnly   (fgDeobf(group = "mezz.jei"                , name = "jei-1.18.1"  , version = "9.1.0.44"      , classifier = "api"))
    runtimeOnly   (fgDeobf(group = "mezz.jei"                , name = "jei-1.18.1"  , version = "9.1.0.44"                          ))
    compileOnly   (fgDeobf(group = "top.theillusivec4.curios", name = "curios-forge", version = "1.18.1-5.0.5.1", classifier = "api"))
    runtimeOnly   (fgDeobf(group = "top.theillusivec4.curios", name = "curios-forge", version = "1.18.1-5.0.5.1"                    ))
}

//Let me have my fun, ok? fg.deobf only supports the ugly one-string form
fun fgDeobf(group: String, name: String, version: String, classifier: String? = null): Dependency {
    return if(classifier == null) {
        fg.deobf("$group:$name:$version")
    } else {
        fg.deobf("$group:$name:$version:$classifier")
    }
}