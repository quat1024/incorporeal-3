Incorporeal 3
=============

(Old dragon yells at corporea spark.)

Currently this is little more than a hellish Gradle dreamscape. No content.

# Build information

**botania build is broken RN because botania-fabric jenkins machine broke** -fabric-snapshot is much older than botania-xplat. Investigate this later.

Ok so, this is by far the most cursed buildscript i've had the pleasure of messing with.

See:
* `buildSrc/src/main/kotlin/incorporeal.java-conventions.gradle.kts` for settings common to all artifacts
* The other files in that folder, for settings common to Fabricness, Forgeness, or commonness
* `(Common|Fabric|Forge)/build.gradle.kts` for settings relevant to "being a Botania addon"
* `gradle.properties` for build variables like name and version number

Keep in mind:

* It's currently open-season for Botania API breaks, and the `1.18.1-428-SNAPSHOT` version I'm depending on right now is just `HEAD`.
* I prefer to develop on Fabric first and fix Forge later. (Although rn Forge works better.)

## things i need to add

* mixin jsons, naming mixin jsons in fabric.mod.json and mixingradle block
* run configurations
  * figure out where the fabric run configs went lol
* a place for generated resources to go, if i end up using a data gen 
* TEST IT MORE, test remapping, test refmaps, test test
* non-`Common` `-sources` jars don't contain `Common` sources (hubry said this happens to patchy and botania too)
* Figure out why deps in `:Common` aren't getting picked up by `:Fabric` (botania-xplat in specific) - is this related to jenkins
* What's the difference between depending on :Common and adding its source set to the compilation classpath. do you need to do both

### Why are you using kotlin buildscripts

Just to feel something.

I also wanted to learn a little more about Gradle, and the additional type-safety and go-to-definition support sometimes helps to understand what is actually happening.

# License

LGPL-3.0-or-later