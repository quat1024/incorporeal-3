plugins {
    id("incorporeal.java-conventions")
    id("org.spongepowered.gradle.vanilla")
}

minecraft {
    val minecraftVersion: String by project
    version(minecraftVersion)
}

