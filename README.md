Incorporeal 3
=============

Magic mod addon, in which an old dragon yells at a corporea spark. 1.18, Forge and Fabric.

Incorporeal 2 (1.16/Forge) is [here](https://github.com/quat1024/incorporeal-2-forge), Incorporeal 1 (1.12/Forge) is [here](https://github.com/quat1024/incorporeal).

## Things that have been ported

* corporea solidifier
* corporea ticket
* corporea retainer evaporator
* red string liar
* item frame tinkerer
* unstable cubes
* Sanvocalia
* The funny
* natural redstone circuitry
* ender soul core, kind of??

## Things left to port

* potion soul core
* ticket conjurer (item, chat mixin, overlay)
* rod of the fractured space (item, entity) (remember the botania rods tag)
* the datapack and lexicon, probably to be cut and paste from 1.16
* redstone root plant
  * maybe change natural circuits to be stonecut from redstone roots instead of dropping
* Redo ender soulcores a little bit. It also needs to correctly drain mana. This will need some work i think

## Things left to design and write

* Computer mod :)

## Things that will not be ported

* frame screw
  * I don't like it and didn't port it for 1.16 either
  * n.b., computermod can probably read/write its rotation
    * will need a mixin to make it properly serialize rotation when the frame is empty
* Rhododendrite
  * I want to go in a different direction
  * will be integrated into the main addon, and not its own thing
* Corporea Soul Core
  * I still like this idea a lot, but want to make it much cheaper to access
  * (tentatively) Just put a corporea spark on a player head
  * ...This could go in Botania

# Build information

Ok so, this is by far the most cursed buildscript i've had the pleasure of messing with.

See:
* `buildSrc/src/main/kotlin/incorporeal.java-conventions.gradle.kts` for settings common to all artifacts
* The other files in that folder, for settings common to Fabricness, Forgeness, or commonness
* `(Common|Fabric|Forge)/build.gradle.kts` for settings relevant to "being a Botania addon"
* `gradle.properties` for build variables like name and version number

Keep in mind:
* It's currently open-season for Botania API breaks, and the `1.18.1-428-SNAPSHOT` version I'm depending on right now is literally just `HEAD`.
  * Builds are not reproducible and may break at any time.
  * Life on the edge! ;)
* I prefer to develop on Fabric first and fix Forge later.

Having weird build issues:
* If `/build` cropped up again (not `/Fabric/build`, just `/build`), yeet it
* Try `./gradlew --refreshDependencies`
* ensure you have the same version of botania-fabric/botania-forge/botania-xplat i.e. check that the jenkins didnt get clogged again

If you're using Eclipse:
* Good luck!
* I heard turning off the Kotlin integration and letting buildship treat it as a black-box might help?
* Sorry about that.

## things i need to add to the buildscript

* forge server run configuration
* Data generators!
  * right now i've basically pasted old datagenned resources from 1.16 into the main tree
  * need a place for generated resources to go as well (i like using a separate source set)
  * i can use vanilla datagen. I don't HAVE to overengineer it. I can just use vanilla datagen. I will probably overengineer it
* TEST IT MORE, test remapping, test refmaps in prod, test test !!
* non-`Common` `-sources` jars don't contain `Common` sources - hubry said this happens to patchy and botania too. why?
* figure out what's the difference between depending on `:Common` and adding its source set to the compilation classpath, and if you need to do both

### Why are you using kotlin buildscripts

Just to feel something

# License

LGPL-3.0-or-later