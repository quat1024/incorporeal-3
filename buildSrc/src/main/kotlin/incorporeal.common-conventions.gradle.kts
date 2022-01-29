plugins {
    id("incorporeal.java-conventions")
    id("org.spongepowered.gradle.vanilla")
}

minecraft {
    //Declare a dependency on Minecraft so the common sourceset
    //has something to compile against. VanillaGradle always
    //uses the official Mojang mappings.
    val minecraftVersion: String by project
    version(minecraftVersion)
}