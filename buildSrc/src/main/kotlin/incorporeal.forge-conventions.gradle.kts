plugins {
    id("incorporeal.java-conventions")
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin")
}

base {
    //Set the archives name.
    archivesName.set("${rootProject.name}-forge")
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
    
    //Use the Mixin annotation processor
    annotationProcessor(group = "org.spongepowered", name = "mixin", version = "0.8.5", classifier = "processor")
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
    //Uhh.. surely at least one of these substutions will catch it
    filesMatching(listOf("mods.toml", ".*mods.toml", "**/mods.toml")) {
        expand("version" to project.version)
    }
}