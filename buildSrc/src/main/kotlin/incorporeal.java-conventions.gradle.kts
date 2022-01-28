plugins {
    java
}

group = "agency.highlysuspect"

repositories {
    mavenCentral()
    
    maven {
        name = "Sponge / Mixin"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
}

java {
    withSourcesJar();
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.jar {
    //random things that might get left around
    exclude("**/*.bat", "**/*.psd", "**/*.exe", "**/*.kra")
    //anything explicitly marked unused
    exclude("**/unused")
    //datagenerator .cache file
    exclude(".cache")
}

tasks.withType<JavaCompile>().configureEach { 
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}