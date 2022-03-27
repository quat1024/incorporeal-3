plugins {
    //Inherit from java-conventions.
    id("incorporeal.java-conventions")
    //get ForgeGradle.
    id("net.minecraftforge.gradle")
    //get MixinGradle.
    id("org.spongepowered.mixin")
}

base {
    //Set the archives name.
    archivesName.set("${rootProject.name}-forge")
}

mixin {
    //Tell MixinGradle the refmap filename I want to use.
    val modId: String by project
    add(sourceSets["main"], "$modId.refmap.json")

    //Also tell it about my mixin.jsons
    config("$modId.mixin.common.json")
    config("$modId.mixin.forge.json")
}

dependencies {
    //Declare a dependency on Minecraft, or rather on Forge's patched version of it.
    val forgeVersion: String by project
    minecraft("net.minecraftforge:forge:$forgeVersion")

    //Declare a dependency on the common source set.
    compileOnly(project(":Common"))
    
    //Use the Mixin annotation processor
    annotationProcessor(group = "org.spongepowered", name = "mixin", version = "0.8.5", classifier = "processor")
}

minecraft {
    //Use official Mojang mappings.
    val minecraftVersion: String by project
    mappings("official", minecraftVersion)
    
    //Add some run configurations!
    //TODO add a server run config lol, shouldnt be too hard look at Crafttweaker
    runs {
        val modId: String by project
        create("client") {
            taskName("Client")
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                    source(project(":Common").sourceSets.main.get())
                }
            }
        }
        
        
    }
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