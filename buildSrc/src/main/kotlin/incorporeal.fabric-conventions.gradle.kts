plugins {
    id("incorporeal.java-conventions")
    id("fabric-loom")
}

dependencies {
    val minecraftVersion: String by project

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    
    implementation(project(":Common"))
}