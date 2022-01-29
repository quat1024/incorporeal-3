plugins {
    id("incorporeal.java-conventions")
    id("fabric-loom")
}

loom {
    //Set the refmap filename. In cursed multiloader projects,
    //Loom isn't very good at picking a nice filenme for the refmap.
    mixin {
        val refmapFileName: String by project
        defaultRefmapName.set(refmapFileName)
    }
}

dependencies {
    //Declare a dependency on Minecraft.
    val minecraftVersion: String by project
    minecraft("com.mojang:minecraft:$minecraftVersion")
    
    //Use official Mojang mappings.
    mappings(loom.officialMojangMappings())
    
    implementation(project(":Common"))
}