plugins {
    //Apply the forge-conventions plugin declared in buildSrc.
    id("incorporeal.forge-conventions")
    //Bring a bunch of maven repositories into scope.
    id("incorporeal.repositories")
}

dependencies {
    val botaniaVersion: String by project
    val botania = implementation(fgDeobf(group = "vazkii.botania", name = "Botania", version = botaniaVersion))

    //If requested, declare Botania as a "changing" dependency.
    //This makes Gradle cache it for a much shorter period of time.
    //See java-conventions for where this is configured.
    val declareBotaniaAsChanging: String by project
    if(declareBotaniaAsChanging == "true" && botania is ExternalModuleDependency) { //kotlin downcast
        botania.isChanging = true
    }
    
    //botania-forge's transitive dependencies as of Aug 20, 2022; https://github.com/VazkiiMods/Botania/blob/05e734c439d1b77985f4cb9a9b1cf414ceebbdbf/Forge/build.gradle
    //Botania does not (currently) publish transitive dependency information for its artifacts.
    //See the bottom of java-conventions for a possible reason why.
    
    //Actually use a newer patchouli, to test https://github.com/quat1024/incorporeal-3/issues/13
    // (at least on forge, getting this into the fabric arm would be handy)
    implementation(fgDeobf(group = "vazkii.patchouli"        , name = "Patchouli"   , version = "1.18.2-71.1"                             ))
    //implementation(fgDeobf(group = "vazkii.patchouli"        , name = "Patchouli"   , version = "1.18.2-67"                             ))
    
    compileOnly   (fgDeobf(group = "mezz.jei"                , name = "jei-1.18.2"  , version = "9.4.3.122"         , classifier = "api"))
    runtimeOnly   (fgDeobf(group = "mezz.jei"                , name = "jei-1.18.2"  , version = "9.4.3.122"                             ))
    compileOnly   (fgDeobf(group = "top.theillusivec4.curios", name = "curios-forge", version = "1.18.2-5.0.6.3"    , classifier = "api"))
    runtimeOnly   (fgDeobf(group = "top.theillusivec4.curios", name = "curios-forge", version = "1.18.2-5.0.6.3"                        ))
    compileOnly   (fgDeobf(group = "com.blamejared.crafttweaker", name = "CraftTweaker-forge-1.18.2", version = "9.1.123"))
    compileOnly   (fgDeobf(group = "com.unascribed", name = "ears-api", version = "1.4.5"))
}

//Let me have my fun, ok? fg.deobf only supports the ugly one-string form
fun fgDeobf(group: String, name: String, version: String, classifier: String? = null): Dependency {
    return if(classifier == null) {
        fg.deobf("$group:$name:$version")
    } else {
        fg.deobf("$group:$name:$version:$classifier")
    }
}