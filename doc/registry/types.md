## KesseractJS - Registry Types

Registry system of KesseractJS is rewritten to support many, many more registry types:

- SoundEvent
- Fluid
- MobEffect
- Block
- Enchantment
- Item
- Potion
- ParticleType
- Custom stat, in the form of ResourceLocation
- Attribute
- VillagerType
- VillagerProfession
- PoiType

## Subtype

For example, in KesseractJS registry system, a sword item, is an item with subtype `sword`, and can be registered with:

```js
onEvent('item.registry', event => {
    event.create('insert_your_item_id_here', 'sword')
})
```

Supported Subtype of block registry:
- detector
- slab
- stairs
- fence
- wall
- fence_gate
- stone_pressure_plate
- stone_button
- wooden_pressure_plate
- wooden_button
- pressure_plate
- button
- falling
- crop
- cardinal (Note: furnace-like block that is horizontally directional)

Supported subtype if Item
- sword
- pickaxe
- axe
- shovel
- shears
- hoe
- helmet
- chestplate
- leggings
- boots
- music_disc

## Example

Item: play some music?

```js
onEvent('item.registry', event => {
    event.create('disc_14', 'music_disc')
        .song('jamiroquai:vitrual_insanity')
        .analogOutput(15)
})
```

Block: metal pipe

```js
onEvent('block.registry', event => {
    event.create('metal_pipe', 'falling')
        .material('anvil')
})
```
