import gradle.kotlin.dsl.accessors._22a1ac937b0d3843d97d89c3b79657a3.minecraft

plugins {
    id("incorporeal.java-conventions")
    id("net.minecraftforge.gradle")
}

configure<net.minecraftforge.gradle.userdev.UserDevExtension> {
    val minecraftVersion: String by project
    mappings("official", minecraftVersion)
}

dependencies {
    val minecraftVersion: String by project
    
    minecraft("net.minecraftforge:forge:$minecraftVersion-39.0.58")
}