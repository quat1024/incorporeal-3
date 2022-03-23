Incorporeal 3
=============

[![the real](https://github.com/quat1024/incorporeal-3/actions/workflows/yeaaa.yml/badge.svg)](https://github.com/quat1024/incorporeal-3/actions/workflows/yeaaa.yml)

Magic mod addon in which an old dragon yells at a corporea spark. I don't think I can call it "corporea focused" anymore. 1.18.2, Forge and Fabric.

Incorporeal 2 (1.16/Forge) is [here](https://github.com/quat1024/incorporeal-2-forge), Incorporeal 1 (1.12/Forge) is [here](https://github.com/quat1024/incorporeal), and because I keep wanting to click on it, Botania is [here](https://github.com/vazkiimods/botania).

## Things that have been ported

* corporetics
  * corporea ticket
  * corporea solidifier
  * Sanvocalia
  * ticket conjurer
  * red string liar
  * item frame tinkerer
  * rod of the fractured space
* natural redstone circuitry
  * redstone root crop (Broken in 1.18.2)
  * natural repeater
  * natural comparator
* soul cores
  * ender soul core
  * blood soul core
  * corporea soul core's *mechanic* (put a corporea spark on a player head). this supplants the block
* decorative/fun items
  * unstable cubes
  * Sweet Alexum

## New things

* clearly you do not own an air fryer
* Red String Constrictor
* Compressed Tiny Potatoes from botania tweaks 1.12 (as well as some uncompressed ones)

## Things left to do

* Finish up computer mod
  * gameplay tweaks
  * documentation
  * models and textures
  * entity support for the thingies
* release:)

## Things that will not be ported

* frame screw
  * I don't like it and didn't port it for 1.16 either
  * n.b., computermod can probably read/write its rotation
    * will need a mixin to make it properly serialize rotation when the frame is empty
* Rhododendrite
  * I want to go in a different direction
  * will be integrated into the main addon, and not its own thing
* Corporea Soul Core
  * I still like this idea a lot, so I made it much cheaper to access
  * Just put a corporea spark on a player head
  * ...This could go in Botania
* Corporea Retainer Evaporator
  * Obsoleted by using two corporea retainers set to the new "Retain Missing" mode and pingponging a request between them
  * I love this mechanic sooooo much lmao
  * Also obsoleted by computermod (when i impl that)

# Build information

Ok so, this is by far the most cursed buildscript i've had the pleasure of messing with.

See:
* `buildSrc/src/main/kotlin/incorporeal.java-conventions.gradle.kts` for settings common to all artifacts
* The other files in that folder, for settings common to Fabricness, Forgeness, or commonness
* `(Common|Fabric|Forge)/build.gradle.kts` for settings relevant to "being a Botania addon"
* `gradle.properties` for build variables like name and version number

Keep in mind:
* The ["xxx: Type by project" syntax](https://docs.gradle.org/current/userguide/kotlin_dsl.html#kotdsl:properties) plucks a value out of `gradle.properties`, I use this file for many project-wide properties.
* The current version of Botania being depended on is `1.18.2-430-SNAPSHOT`, a.k.a, basically Botania `1.18.x` `HEAD`.
  * Jared's jenkins doesn't store `-SNAPSHOT` versions for an indefinite amount of time, so the build may break.
  * The API surface may change out from under me, which will also cause the build to break.
  * The Botania version may be set in `gradle.properties`.
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
* non-`Common` `-sources` jars don't contain `Common` sources
  * https://github.com/VazkiiMods/Botania/commit/48103b54fe2ff2c9b34b021fb3a8c7ca106fdb67 but i need to make it work on kotlin buildscripts lmao
* figure out what's the difference between depending on `:Common` and adding its source set to the compilation classpath, and if you need to do both

### Why are you using kotlin buildscripts

Just to feel something

# License

LGPL-3.0-or-later