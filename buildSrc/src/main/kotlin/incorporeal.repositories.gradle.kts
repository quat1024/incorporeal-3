repositories {
    //botania-xplat, botania-fabric, patchouli
    maven {
        name = "Jared"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("vazkii.botania")
            includeGroup("vazkii.patchouli")
        }
    }
    
    //Fiber
    maven {
        name = "modmuss50"
        url = uri("https://maven.modmuss50.me")
        content {
            includeGroup("me.zeroeightsix")
        }
    }
    
    //Cardinal Components
    maven {
        name = "Ladysnake"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
        content {
            includeGroup("io.github.onyxstudios.Cardinal-Components-API")
            includeGroup("dev.onyxstudios.cardinal-components-api") //i guess they moved it
        }
    }
    
    //Trinkets
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
        content {
            includeGroup("dev.emi")
            
            //At the moment, required to pick up Mod Menu
            // https://github.com/emilyploszaj/trinkets/issues/159
            includeGroup("com.terraformersmc")
        }
    }
    
    //Reach Entity Attributes
    maven {
        name = "jamieswhiteshirt"
        url = uri("https://maven.jamieswhiteshirt.com/libs-release/")
        content {
            includeGroup("com.jamieswhiteshirt")
        }
    }
    
    //Step Height Entity Attribute
    maven {
        name = "jitpack :("
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.emilyploszaj")
        }
    }
    
    //Roughly Enough Items and friends (someone includes this transitively)
    maven {
        name = "shedaniel"
        url = uri("https://maven.shedaniel.me")
        content {
            includeGroup("me.shedaniel")
            includeGroup("me.shedaniel.cloth")
            includeGroup("dev.architectury")
        }
    }
    
    //JEI
    maven {
        name = "progwml6"
        url = uri("https://dvs1.progwml6.com/files/maven")
        content {
            includeGroup("mezz.jei")
        }
    }
    
    //Curios
    maven {
        name = "theillusivec4"
        url = uri("https://maven.theillusivec4.top/")
        content {
            includeGroup("top.theillusivec4.curios")
        }
    }
}