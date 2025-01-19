# KesseractJS - Events

## JS Events

- BlockLandEventJS: `block.landing`
- AtlasSpriteRegistryEventJS: `client.atlas_stitch`
- GenerateClientAssetsEventJS: `client.generate_asset`
- LangEventJS: `client.lang`
- LightningStrikeEventJS: `world.lightning.strike`
- CommandRegistryEventJS: `command.registry`
- CreativeTabRegistryEventJS: `tab.registry`

## Native Events

[EventJS](https://www.curseforge.com/minecraft/mc-mods/eventjs) is integrated into KesseractJS

EventJS makes native event listening reloadable and optionally sided. With EventJS, you can:

- refresh your native event listeners without restarting the whole game or store the handler within a `global['someId']`, which is both simpler and more performant.
- add/remove native event listeners as you like without relaunching, which was impossible in KubeJS for 1.16-1.20
- prevent your game from crashing if something goes wrong in your native event listener
- add event listener only in server/client side

old style `onForgeEvent` and new binding `NativeEvents` are both supported. `NativeEvents` is avaliable for all 3 script types