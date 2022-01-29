# Multiloader mods, in general

Ok so here's how this concept works.

* Three subprojects, named `Common`, `Fabric`, and `Forge`.
* `Common` uses Sponge's project VanillaGradle to produce a decompiled, remapped-with-official-names Minecraft artifact. The `Common` project depends on this artifact mainly so I have something to compile against.
* `Fabric` sets up a Loom project, and `Forge` sets up a ForgeGradle project. These projects also declare a dependency on Minecraft (or MinecraftForge) and also use the official Mojang mappings in their respective ecosystem. They both add `Common`'s source set to their compilation classpath, include `Common`'s resources, and there's a little song and dance relating to Mixin, but they are otherwise unremarkable Loom/ForgeGradle projects.

The effect:

* Anything you write in `Common` gets included into both .jars, including resources/data and Mixins. But you don't have access to any loader-specific facilities in that source set.
* You can write things in the `Fabric` and `Forge` source sets to include them in only that loader's .jar, and can enjoy access to loader-specific facilities.
* You produce three artifacts: a distributable Fabric mod; a distributable Forge mod; and a `common` .jar that contains the things common to both, which is not runnable but is useful as a Maven artifact for like, any doofus who wants to write a multi-loader addon for your multi-loader mod (lol, who would do that)

AFAIK [this is a concept pioneered by Jared and Darkhax](https://github.com/jaredlll08/MultiLoader-Template), and it has a little bit of prior art with... whatever Architectury is doing, this project uses stock Loom and ForgeGradle though. I also cribbed a lot from [Botania](https://github.com/VazkiiMods/Botania), which is probably one of the largest multi-loader mods right now that doesn't use Architectury. If you're interested in setting up maven publication (which i don't use) they use it in Botania so idk figure it out.

# This approach, in specific

This is a `buildSrc` folder; my understanding is that Gradle compiles the contents of this folder first, then makes it available to the other buildscripts in the parent repo such that files in `src/main/(groovy|kotlin)` appear as plugins to other builds.gradles. Applying them sorta, inherits from them? (it doesn't bring their functions/symbols into scope though which is odd.) This is called a "precompiled script plugin" in the Gradle lingo. They can do most of the things "real" gradle plugins can do, I think? pretty nice.

So here I declare four Gradle plugins. `java-conventions` is the root plugin, and `common-`, `fabric-` and `forge-conventions` each apply it. The `Common`, `Fabric`, and `Forge` subprojects each apply their respective `conventions` plugin.

In the subprojects, I have a dedicated space to put mod dependencies (botania, trinkets, etc) separate from other dependencies like minecraft or fabric-loader deps, and I can add any other bits that are *specific* to building Incorporeal 3 the Botania addon, as opposed to any other multiloader mod. That's the goal of this whole scheme, really, I can separate things special about building my mod, from things special about building multiloader mods, from things special about building Java applications in general.

Of course the downside of this splitting scheme is, well, things are split, the build process is now scattered across 10 different files. I hope it makes enough sense to be navigable.

## `build.gradle.kts`

Naturally the `buildSrc` folder is itself a Gradle project, one that builds Gradle plugins. Turtles all the way down and all that. Here I just declare dependencies; `kotlin-dsl` is a required dependency of all gradle plugins that use `.gradle.kts` files I think, and I also declare dependencies on the third-party plugins that my script plugins will apply.

## `incorporeal.java-conventions.gradle.kts`

Things in here are inherited by the other `-conventions` buildscripts, so its definitions apply to all three subprojects.

Here is where I set up a few things that apply to all Java sources and artifacts I want to build, like the artifact version number, language level, and a few other odds and ends. If I used a code style plugin, this would be a good place to configure it. Would also a good place to stick things like jetbrains/findbugs annotation deps.

## `incorporeal.common-conventions.gradle.kts`

Here is where I apply and configure the VanillaGradle plugin. This plugin does one thing and one thing only: adds a mapped-to-Mojang-names decompiled Minecraft artifact to the project's dependencies. I need this to have something to write code against in `Common`'s source sets.

## `incorporeal.(fabric|forge)-conventions.gradle.kts`

Here is where I apply and configure the Loom and ForgeGradle/MixinGradle plugins. In each ecosystem, I

* declare a dependency on Minecraft (or MinecraftForge),
* choose to use the official Mojang mappings,
* instruct the Mixin plugin to use a particular refmap filename,
  * Loom tends to do a bad job automatically picking the filename in multiloader projects, defaulting to the same name as the produced `.jar` + `.refmap.json` which is a very silly name, and Forge requires you to choose the refmap filename explicitly through MixinGradle anyways
* include the Common source set onto the compilation classpath,
* include the Common resources into the project's resources,
* set `archivesName` to include a `-fabric` or `-forge` suffix,
* set up a `processResources` block, to substitute in the mod version into the `fabric.mod.json`/`mods.toml` (this is kinda crappy because im bad at gradle).

In fabric-conventions I add a dep on fabric-loader. FG requires manually declaring run configs so this is also where that happens (TODO).

Basically just, all the stuff you do any time you write any mod (with mixins).

(TODO: in forge conventions I need to mention my mixin.jsons) 

## `incorporeal.repositories.gradle.kts`

ok this is less "Gradle plugin", more "huge `repositories { }` block that gets included in everything so I don't have half a dozen `repositories` blocks scattered all over the place". Each one is configured with `includeGroup`, filtering it to the things that actually come out of that repository.

The only other `repositories` block is in `build.gradle.kts`, used to fetch VanillaGradle/Loom/ForgeGradle itself. Those are also filtered, except for `gradlePluginPortal()` because gradle plugins tend to have 100 million transitive deps.