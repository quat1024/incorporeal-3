plugins {
    //Apply the common-conventions plugin declared in buildSrc.
    id("incorporeal.common-conventions")
    //Bring a bunch of maven repositories into scope.
    id("incorporeal.repositories")
}

dependencies {
    //botania-xplat - Multiloader Botania code
    implementation(group = "vazkii.botania", name = "Botania-xplat", version = "1.18.1-428-SNAPSHOT").isChanging = true
}