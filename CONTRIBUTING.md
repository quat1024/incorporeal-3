# Issues

Thanks for reporting an issue! Sorry you had to do that in the first place.

Please include Botania version, Incorporeal version (or commit hash), your modloader; and if you think they will be relevant, your `latest.log` and a list of other mods you have installed. Please be available on the issue tracker if there are any followup questions.

At this time I cannot handle any issues for Incorporeal 1 (1.12), and Incorporeal 2 (1.16) is for bugfixes only. Please report those to their respective repos. Sorry about that.

# Feature requests

Thank you for contributing an idea! Just throw these in the "issues" section.

Keep in mind that Incorporeal attempts to match or beat the puzzliness level of Botania, so I'd like to avoid adding a "magic block" that is always the best way to solve a particular problem. But, "already being able to solve it in a more 'interesting' way" does not preclude adding a convenience if it makes the game more fun or understandable.

# Art

Thank you for contributing art! The bulk of the textures are programmer art, I am always looking for nicer art. Although... at this time, consider spending your time on Botania's retexture effort instead :)

* My main request: instead of placing textures directly in `textures/block` or `textures/item`, please make a subdirectory. This helps keep things organized.
* If a file you want to edit is goverened by a data generator, and you don't want to edit the Java that generates it, feel free to just delete the one in `src/generated` and I'll make it work.

Feel free to add yourself to the credits (in `fabric.mod.json` on Fabric and `mods.toml` on Forge). Yes, even if you only changed a tiny thing.

# Translation / Language entries

Thank you for contributing a translation!

The same as Botania's policy: If you do not translate a language entry from `en_us.json`, simply leave it out, instead of copying-and-pasting.

The Patchouli book .jsons are created with a data generator. If you must override a page, please place it in `Common/src/main/resources/data/incorporeal/patchouli_books/lexicon/<your language code>/entries`, instead of editing anything in `Common/src/generated`. I can move the files for you, if you want.

Feel free to add yourself to the credits (in `fabric.mod.json` on Fabric and `mods.toml` on Forge). Yes, even if you only changed a tiny thing.

# Code

Thank you for contributing code!

I heard the multi-loader setup does not work too well in Eclipse. You will probably have a better experience with IntelliJ.

Some things I'm always looking for:

* ports of the remaining 1.12/1.16 features
* ports of relevant "Botania Tweaks" features
* feature reimplementations in a less hacky way (like if Botania adds a public API for something)
* bug fixes and polish
* `//TODO` items
* documentation

As far as code style:

* Tabs for indentation, please.
* Comment liberally.
  * Prefer Javadoc that looks good in the editor, instead of muddying it up with html tags.
* When adding a new class, prefer a "wide" package hierarchy instead of a "deep" one, unless there's a really good reason (like classes relating to optional mod compat).
  * This is a new thing i'm trying because I think it makes the codebase easier to browse.
* Please use globally unique class names. Anything you might call "ModItems", "ModBlocks" etc, use the prefix "Inc" instead, to disamgibuate it from Botania's.
* I haven't bothered to formalize the rest of my code-style preferences in the form of auto-formatter or linter rules. Don't sweat it, just make it readable, I don't mind if the code style is not 100% uniform across the mod and I can always edit it later.

Feel free to add yourself to the credits (in `fabric.mod.json` on Fabric and `mods.toml` on Forge). Yes, even if you only changed a tiny thing.