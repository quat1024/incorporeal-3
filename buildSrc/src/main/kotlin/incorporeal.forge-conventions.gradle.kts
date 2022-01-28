plugins {
    id("incorporeal.java-conventions")
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin")
}

configure<net.minecraftforge.gradle.userdev.UserDevExtension> {
    val minecraftVersion: String by project
    mappings("official", minecraftVersion)
}

mixin {
    val refmapFileName: String by project
    
    add(sourceSets["main"], refmapFileName)
}

dependencies {
    val minecraftVersion: String by project
    
    minecraft("net.minecraftforge:forge:$minecraftVersion-39.0.58")
}