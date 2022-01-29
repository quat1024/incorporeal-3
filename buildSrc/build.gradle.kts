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
    implementation("fabric-loom:fabric-loom.gradle.plugin:0.10-SNAPSHOT")
    implementation("org.spongepowered.gradle.vanilla:org.spongepowered.gradle.vanilla.gradle.plugin:0.2.1-SNAPSHOT")
    implementation("net.minecraftforge.gradle:ForgeGradle:5.1.+")
    implementation("org.spongepowered:mixingradle:0.7-SNAPSHOT")
}