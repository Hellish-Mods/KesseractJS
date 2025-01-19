# KesseractJS - Fake Mod

```js
// In startup_scripts

// register a fake mod
Platform.registerFakeMod("notarealmodid").displayName("Hello World!")
// let's register a item with a matched modid to see its effects
onEvent('item.registry', event => {
	event.create('notarealmodid:thing')
})
```
