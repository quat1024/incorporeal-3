plugins {
    id("incorporeal.java-conventions")
    id("fabric-loom")
}

loom {
    mixin {
        val refmapFileName: String by project
        defaultRefmapName.set(refmapFileName)
    }
}

dependencies {
    val minecraftVersion: String by project

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    
    implementation(project(":Common"))
}