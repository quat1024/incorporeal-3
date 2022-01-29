//Apply the Java plugin.
plugins {
    java
    id("incorporeal.repositories")
}

//Set the artifact's version.
//Here I use the Botania convention of "1.18.1-0", which is not actually Semver or anything. Sue me.
//You could also set "version" directly in gradle.properties, but i like having the mc version thrown in there too.
val minecraftVersion: String by project
val buildNumber: String by project
version = "$minecraftVersion-$buildNumber"

java {
    //Declare that Java artifacts should have a sources jar published alongside them.
    //I don't use Maven publication atm but if I did this would be handy.
    withSourcesJar()
    
    //Use Java version 17 for compilation and building tasks.
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.jar {
    //Random things that might get left around if I'm sloppy.
    exclude("**/*.bat", "**/*.psd", "**/*.exe", "**/*.kra")
    //Anything explicitly marked unused.
    exclude("**/unused")
    //Vanilla datagenerator .cache file.
    exclude(".cache")
}

//Not sure if this is needed, given the toolchian block above. Doesn't hurt?
tasks.withType<JavaCompile>().configureEach { 
    options.encoding = "UTF-8"
    options.release.set(17)
}

//I *believe* that the purpose of this bit is because projects like Loom and FG use a lot of magic (that you're
//really not supposed to be able to do with Gradle) to generate a remapped version of any mods you depend on,
//but gradle publishing tasks have no idea that this "botania-remapped-kasdklsjkdj" dependency should actually
//correspond to a dependency on only "botania" for anyone consuming the artifact.
//I don't think this can really be fixed in a not-fragile not-horrible way, so we simply instruct Gradle
//to not publish any data about transitive dependencies at all. Consumers will need to redeclare these
//dependencies in their projects if they want to be able to run the game, which sucks, but isn't the end of the world.
//See: https://github.com/AppliedEnergistics/Applied-Energistics-2/issues/5259
tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}