# KesseractJS
### Welcome to the 4th dimension.

[![cf](https://cf.way2muchnoise.eu/full_kesseractjs_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/kesseractjs)
[![actions](https://github.com/hellish-mods/kesseractjs/actions/workflows/build_1605.yml/badge.svg)](https://github.com/hellish-mods/kesseractjs)

[![logo](https://media.forgecdn.net/avatars/1002/899/638523320288453902.gif)](https://www.curseforge.com/minecraft/mc-mods/kesseractjs)

**KesseractJS** is a fork of [KubeJS](https://kubejs.com/), continuing its development for 1.16.5. It backport a lot of different features from the newer versions, and even adds some that are not present at all in the original mod. The latter are documented [on a dedicated wiki](https://wiki.modernmodpacks.site/wiki/hellish-mods/kesseractjs), and support for those are offered on [our discord server](https://discord.modernmodpacks.site), so don't be afraid to ask there.

This mod depends on a modified fork of the Rhino JS parser called [Rhizo](https://curseforge.com/minecraft/mc-mods/rhizo), please install it before use. We also highly recommend you use [ProbeJS Legacy](https://curseforge.com/minecraft/mc-mods/probejs-legacy) during script development for better IDE support.

This project was made as a collaboration between [Hellish Mods](https://github.com/Hellish-Mods), [ZZZank](https://github.com/zzzank), and [MundM2007](https://github.com/mundm2007), with the occasional help from [Team Potato](https://github.com/MCTeamPotato). Thank you guys <3

## Feature showcase

To be honest, there are too many. So we're only showcasing some features here, there's a much more complete feature/fix list in our doc

### [Doc Index](./doc/index.md)

* Command Registry

```js
onEvent('command.registry', event => {
    const Commands = event.getCommands()
    const Arguments = event.getArguments()
    
    event.register(Commands.literal("say_wow")
        .excutes(context => {
            const player = context.source.playerOrException.asKJS()
            player.tell("wow")
        }))
})
```

* Fake mod registration and Mod display name editing

```js
// In startup_scripts

Platform.getInfo('kubejs').name = "CircleJS"

Platform.registerFakeMod("notarealmodid").displayName("Hello World!")
onEvent('item.registry', event => {
	event.create('notarealmodid:thing')
})
```

![screenshot](https://raw.githubusercontent.com/Hellish-Mods/KesseractJS/refs/heads/main/assets/fakemod.png)

* Backports

```js
// Welcome to the future babyyyy

// Falling block type
onEvent('block.registry', event => event.create('metal_pipe', 'falling').material('anvil'))

// Custom music discs (https://github.com/KubeJS-Mods/KubeJS/issues/491)
onEvent('item.registry', event => event.create('disc_14', 'music_disc').song('jamiroquai:vitrual_insanity'))

// thickTexture/thinTexture and textureThick/textureThin intercompatibility
onEvent('fluid.registry', event => {
  event.create('you_dont_want_to_know')
    .thickTexture(0xFF0000)
    .textureThick(0xFF0000) // Both methods work!
})
// Modern Math Helpers
KMath.clamp(3.4567, 4, 5) // clamp a number
KMath.v3d(0.1, 2,3, 4,5) // create a Vertor3d
KMath.m4f() // create a Matrix4f

// + more!
```

* Fixes. Lots of 'em.

for example, fail-safe for ResourceLocation wrapping and recipe id, this can fix recipe builder with invalid id failing silently and block code execution

* Full drag-and-drop compatability with old Kube scripts
* Continuous updates. More in the future!

---

[![MMLogo](https://raw.githubusercontent.com/Modern-Modpacks/assets/main/big_logo.png)](https://modernmodpacks.site)
