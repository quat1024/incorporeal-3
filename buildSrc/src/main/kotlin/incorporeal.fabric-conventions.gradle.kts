plugins {
    //Inherit java-conventions.
    id("incorporeal.java-conventions")
    //get Fabric Loom.
    id("fabric-loom")
}

base {
    //Set the archives name. (nee "archivesBaseName", which will be removed in Gradle 8 apparently)
    archivesName.set("${rootProject.name}-fabric")
}

loom {
    //Set the refmap filename. In cursed multiloader projects, Loom isn't very good at picking a nice filename for the refmap.
    mixin {
        val modId: String by project
        defaultRefmapName.set("$modId.refmap.json")
    }
    
    //Loom doesn't generate run configs by default in subprojects.
    runs {
        configureEach {
            ideConfigGenerated(true)
        }
    }
}

dependencies {
    //Declare a dependency on Minecraft.
    val minecraftVersion: String by project
    minecraft("com.mojang:minecraft:$minecraftVersion")
    
    //Declare a dependency on the common source set.
    compileOnly(project(":Common"))
    
    //Use official Mojang mappings.
    mappings(loom.officialMojangMappings())
    
    //Get fabric-loader.
    val fabricLoaderVersion: String by project
    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
}

tasks.withType<JavaCompile>().configureEach {
    //Add the contents of :Common's main source set to this project's compilation classpath.
    //Btw, with Groovy gradle scripts it looks like this: source(project(":Common").sourceSets.main.allSource)
    //Kotlin just has to be tricky about type-safe extensions. I think this is managed by the Java plugin
    //and isn't really part of Gradle proper.
    source(project(":Common").extensions.getByType(SourceSetContainer::class)["main"].allSource)
}

tasks.processResources {
    //Add the contents of :Common's resources to this project's resources.
    from(project(":Common").extensions.getByType(SourceSetContainer::class)["main"].resources)
    
    //Substitute the artifact version into fabric.mod.json.
    //I dont know how to use gradle btw surely one of these matchers will get it
    filesMatching(listOf("fabric.mod.json", ".*fabric.mod.json", "**/fabric.mod.json")) {
        expand("version" to project.version)
    }
}