import gradle.kotlin.dsl.accessors._22a1ac937b0d3843d97d89c3b79657a3.processResources

plugins {
    id("incorporeal.java-conventions")
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin")
}

base {
    //Set the archives name.
    archivesName.set("$name-forge")
}

mixin {
    //Tell MixinGradle the refmap filename I want to use.
    val refmapFileName: String by project
    add(sourceSets["main"], refmapFileName)
}

dependencies {
    //Declare a dependency on Minecraft, or rather on Forge's patched version of it.
    val minecraftVersion: String by project
    minecraft("net.minecraftforge:forge:$minecraftVersion-39.0.58")
}

configure<net.minecraftforge.gradle.userdev.UserDevExtension> {
    //Use official Mojang mappings.
    val minecraftVersion: String by project
    mappings("official", minecraftVersion)
}

tasks.withType<JavaCompile>().configureEach {
    //Add the contents of :Common's main source set to this project's compilation classpath.
    source(project(":Common").extensions.getByType(SourceSetContainer::class)["main"].allSource)
}

tasks.processResources {
    //Add the contents of :Common's resources to this project's resources.
    from(project(":Common").extensions.getByType(SourceSetContainer::class)["main"].resources)

    //Substitute the artifact version into mods.toml.
    filesMatching("mods.toml") {
        expand("version" to project.version)
    }
}