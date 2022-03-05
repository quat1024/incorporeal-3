plugins {
    //Apply the common-conventions plugin declared in buildSrc.
    id("incorporeal.common-conventions")
    //Bring a bunch of maven repositories into scope.
    id("incorporeal.repositories")
}

dependencies {
    //botania-xplat - Multiloader Botania code
    val botaniaVersion: String by project;
    implementation(group = "vazkii.botania", name = "Botania-xplat", version = botaniaVersion).isChanging = true
}

sourceSets["main"].resources {
    //Include generated resources
    srcDir("src/generated/resources")
}