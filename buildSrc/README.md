# Multiloader mods

Here's how this project works:

* Three subprojects, named `Common`, `Fabric`, and `Forge`.
* `Common` uses Sponge's project VanillaGradle to produce a decompiled, remapped-with-official-names Minecraft artifact. The `Common` project depends on this artifact mainly so I have something to compile against.
* `Fabric` sets up a Loom project, and `Forge` sets up a ForgeGradle project. These projects also declare a dependency on Minecraft (or MinecraftForge) and also use the official Mojang mappings in their respective ecosystem. They both add `Common`'s source set to their compilation classpath, include `Common`'s resources, and there's a little song and dance relating to Mixin, but they are otherwise unremarkable Loom/ForgeGradle projects.

The effect:

* Anything you write in `Common` gets included into both .jars, including resources/data and Mixins. But you don't have access to any loader-specific facilities in that source set.
* You can write things in the `Fabric` and `Forge` source sets to include them in only that loader's .jar, and can enjoy access to loader-specific facilities.
* You produce three artifacts: a distributable Fabric mod; a distributable Forge mod; and a `common` .jar that contains the things common to both, which is not runnable but is useful as a Maven artifact for like, any doofus who wants to write a multi-loader addon for your multi-loader mod (lol, who would do that)

AFAIK [this is a concept pioneered by Jared and Darkhax](https://github.com/jaredlll08/MultiLoader-Template), and it has a little prior art with... whatever Architectury is doing. This project uses stock Loom and ForgeGradle though. I also cribbed a lot from [Botania](https://github.com/VazkiiMods/Botania), which is probably one of the largest multi-loader mods

# This project

This is a `buildSrc` folder; my understanding is that Gradle compiles the contents of this folder first, then makes it available to the other buildscripts in the parent repo as pluigns.  Things in `src/main/(groovy|kotlin)` can be applied as `plugins` to like, sort of inherit from them or bring their definitions into scope? This is called a "precompiled script plugin" I think.

So here I declare ~~four~~ five Gradle plugins. `java-conventions` is the root, and `common-`, `fabric-` and `forge-conventions` each apply that one, inheriting its definitions. The `Common`, `Fabric`, and `Forge` subprojects apply each of these plugins respectively.

The goal of this layout is to, as cleanly as possible, delineate:

* settings that belong to this version of *this* mod, such as its name and version number
* Gradle scaffolding for *this* mod, such as a dependency on Botania
* Gradle scaffolding for the general process of writing a multi-loader mod
* Gradle scaffolding for writing a releasable Java 17 project

## `incorporeal.repositories.gradle.kts`

Literally just a huge `repositories { }` block that gets included in everything, so I don't have half a dozen `repositories` blocks scattered all over the place. Each one is configured with `includeGroup`, filtering it to the things that actually come out of that repository.

The only other `repositories` block is in `build.gradle.kts`, used to fetch VanillaGradle/Loom/ForgeGradle itself.

## `build.gradle.kts`

Naturally the `buildSrc` folder is itself a Gradle project, so the `build.gradle.kts` and `settings.gradle.kts` files adjacent to this `README` refer to the process of compiling this build script. It's turtles all the way down. Here is where I specify Maven coordinates for VanillaGradle, ForgeGradle, and Loom, so that I can apply and configure them from the Gradle plugins in this folder.

## `incorporeal.java-conventions.gradle.kts`

Things in here are inherited by the other `-conventions` buildscripts, so its definitions apply to all three subprojects. Here is where I set up a few things that apply to all Java sources and Java artifacts. I also include `incorporeal.repositories` in this file.

If I used a code style plugin, this would probably be a good place to configure it. Would also a good place to stick things like jetbrains/findbugs annotations. might get to that eventually.

## `incorporeal.common-conventions.gradle.kts`

Here is where I apply and configure the VanillaGradle plugin. This adds a dependency on Minecraft using the official Mojang mappings, mainly so I have something to write code against in the `Common` subproject's sources.

## `incorporeal.(fabric|forge)-conventions.gradle.kts`

Here is where I apply and configure the Loom and ForgeGradle plugins. In each ecosystem, I

* declare a dependency on Minecraft
* choose to use the official Mojang mappings
* instruct the Mixin plugin to use a particular refmap filename (Loom tends to do a bad job automatically picking the filename in multiloader projects, defaulting to the same name as the produced `.jar` + `.refmap.json` which is a very silly name, and Forge requires you to choose the refmap filename explicitly through MixinGradle anyways)
* include the Common source set onto the compile classpath, and the Common resources into the resources
* set up a `processResources` to substitute in the mod version into the `fabric.mod.json`/`mods.toml` (**Except this doesnt work for some reason lol**)

In fabric-conventions I get fabric-loader. In forge-conventions I set up MixinGradle.