plugins {
    //Inherit from java-conventions.
    id("incorporeal.java-conventions")
    //get VanillaGradle.
    id("org.spongepowered.gradle.vanilla")
}

base {
    //Set the archives name. (nee "archivesBaseName", which will be removed in Gradle 8 apparently?)
    //Note that the common .jar is not distributed to end users, but still has some utility as
    //like, a Maven artifact.
    archivesName.set("${rootProject.name}-common")
}

minecraft {
    //Declare a dependency on Minecraft, so the common sourceset has something to compile against.
    //VanillaGradle always uses the official Mojang mappings.
    val minecraftVersion: String by project
    version(minecraftVersion)
}

dependencies {
    //Declare a dependency on Mixin, so they can be written in the common source set.
    compileOnly(group = "org.spongepowered", name = "mixin", version = "0.8.5")
}