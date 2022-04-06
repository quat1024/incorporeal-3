plugins {
    //Apply the common-conventions plugin declared in buildSrc.
    id("incorporeal.common-conventions")
    //Bring a bunch of maven repositories into scope.
    id("incorporeal.repositories")
}

dependencies {
    //botania-xplat - Multiloader Botania code
    val botaniaVersion: String by project
    val botania = implementation(group = "vazkii.botania", name = "Botania-xplat", version = botaniaVersion)

    //If requested, declare Botania as a "changing" dependency.
    //This makes Gradle cache it for a much shorter period of time.
    //See java-conventions for where this is configured.
    val declareBotaniaAsChanging: String by project
    if(declareBotaniaAsChanging == "true") {
        botania.isChanging = true
    }
}