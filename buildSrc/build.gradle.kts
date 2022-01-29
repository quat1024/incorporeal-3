plugins {
    `kotlin-dsl`
}

repositories {
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
        content {
            includeGroup("fabric-loom")
            includeGroupByRegex("^net\\.fabricmc")
        }
    }

    maven {
        name = "Sponge / Mixin"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
        content {
            includeGroup("org.spongepowered.gradle.vanilla")
            includeGroup("org.spongepowered")
        }
    }

    maven {
        name = "MinecraftForge"
        url = uri("https://maven.minecraftforge.net")
        content {
            //language=RegExp
            includeGroupByRegex("^net\\.minecraftforge.*")
        }
    }

    gradlePluginPortal() //This one is not includeGroup allowlisted, just because there's so MUCH downloaded from it lmao
}

dependencies {
    //buildSrc project needs to depend on all the plugins that will be passed onto the subprojects?
    //Or something.
    implementation(group = "fabric-loom"                     , name = "fabric-loom.gradle.plugin"                     , version = "0.10-SNAPSHOT")
    implementation(group = "org.spongepowered.gradle.vanilla", name = "org.spongepowered.gradle.vanilla.gradle.plugin", version = "0.2.1-SNAPSHOT")
    implementation(group = "net.minecraftforge.gradle"       , name = "ForgeGradle"                                   , version = "5.1.+")
    implementation(group = "org.spongepowered"               , name = "mixingradle"                                   , version = "0.7-SNAPSHOT")
}