plugins {
    id("incorporeal.java-conventions")
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin")
}

configure<net.minecraftforge.gradle.userdev.UserDevExtension> {
    //Use official Mojang mappings.
    val minecraftVersion: String by project
    mappings("official", minecraftVersion)
}

mixin {
    //Set the refmap file name. This is mainly explicitly specified
    //for consistency with Fabric Loom, which picks a weird filename by default.
    val refmapFileName: String by project
    add(sourceSets["main"], refmapFileName)
}

dependencies {
    //Declare a dependency on Minecraft, or rather Forge's patched version of it.
    val minecraftVersion: String by project
    minecraft("net.minecraftforge:forge:$minecraftVersion-39.0.58")
}