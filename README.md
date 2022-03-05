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
* the datapack and lexicon
* redstone root crop
* corporea soul core's *mechanic* (now just a corporea spark on player head), not the block itself
* ticket conjurer
* rod of the fractured space

## Things left to do

* ticket conjurer's HUD overlay when you're holding one
* properly impl potion soul core
* properly impl ender soul core
* properly impl red string liar..?

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
* The current version of Botania being depended on is `1.18.1-430-SNAPSHOT`, a.k.a, Botania `HEAD`.
  * Jared's jenkins doesn't store `-SNAPSHOT` versions for an indefinite amount of time. The version may be changed in `gradle.properties`.
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
* TEST IT MORE, test remapping, test refmaps in prod, test test !!
* non-`Common` `-sources` jars don't contain `Common` sources - hubry said this happens to patchy and botania too. why?
* figure out what's the difference between depending on `:Common` and adding its source set to the compilation classpath, and if you need to do both

### Why are you using kotlin buildscripts

Just to feel something

# License

LGPL-3.0-or-later