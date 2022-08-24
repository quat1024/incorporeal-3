Incorporeal 3
=============

Magic mod addon in which an old dragon yells at a corporea spark. I don't think I can call it "corporea focused" anymore. 1.18.2, Forge and Fabric.

Incorporeal 2 (1.16/Forge) is [here](https://github.com/quat1024/incorporeal-2-forge), Incorporeal 1 (1.12/Forge) is [here](https://github.com/quat1024/incorporeal), and because I keep wanting to click on it, Botania is [here](https://github.com/vazkiimods/botania).

[![the real](https://github.com/quat1024/incorporeal-3/actions/workflows/yeaaa.yml/badge.svg)](https://github.com/quat1024/incorporeal-3/actions/workflows/yeaaa.yml)

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
  * redstone root crop
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
* All new computermod that isnt like rhododendrite (Its Bad!!!)
* Corporea Pylon
* Bound Ender Pearl
* Petal block carpets lol

## Things left to do

* Finish up computer mod
  * gameplay tweaks
  * documentation
  * models and textures
* release:)

## Things that will not be ported

* frame screw
  * I don't like it and didn't port it for 1.16 either
  * Computermod can read/write the rotation of frames
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

Keep in mind I prefer to develop on Fabric first and fix Forge later.

Having weird build issues:
* If `/build` cropped up again (not `/Fabric/build`, just `/build`), yeet it
* Try `./gradlew --refreshDependencies`
* ensure you have the same version of botania-fabric/botania-forge/botania-xplat i.e. check that the jenkins didnt get clogged again

If you're using Eclipse:
* Good luck!
* Sorry about that.

### ~~Why are you using kotlin buildscripts~~

~~Just to feel something~~ NOT ANYMORE!!!! Was a fun learning project but got really sick of it.

# License

LGPL-3.0-or-later