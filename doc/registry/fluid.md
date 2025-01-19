## KesseractJS - Fluid Registry

- custom render type via `builder.renderType(xxx)`, currently accepts:
    - cutout
    - cutout_mipped
    - translucent
```js
onEvent('fluid.registry', event => {
    event.create('huh')
        .renderType('translucent')
})
```
- thickTexture/thinTexture and textureThick/textureThin inter-compatibility

```js
onEvent('fluid.registry', event => {
    event.create('you_dont_want_to_know')
        .thickTexture(0xFF0000)
        .textureThick(0xFF0000) // Both methods work!
})
```

- added `.noBucket()` and `.noBlock()` to Fluid Builder
