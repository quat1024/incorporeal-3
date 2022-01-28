plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()

    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }

    maven {
        name = "Sponge / Mixin"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
        //todo: why does includeGroup break this
    }

    maven {
        name = "MinecraftForge"
        url = uri("https://maven.minecraftforge.net")
    }
}

dependencies {
    //buildSrc project needs to depend on all the plugins that will be passed onto the subprojects?
    //Or something.
    implementation("fabric-loom:fabric-loom.gradle.plugin:0.10-SNAPSHOT")
    implementation("org.spongepowered.gradle.vanilla:org.spongepowered.gradle.vanilla.gradle.plugin:0.2.1-SNAPSHOT")
    //Forgegradle is weird
    implementation("net.minecraftforge.gradle:ForgeGradle:5.1.+")
}