## KesseractJS - Fixing

- prevent toLowerCase() from breaking ResourceLocation due to unusual locale, related: [notion note](https://www.notion.so/modernmodpacks/KesseractJS-d08c3cc6e49d4221ba6d0b759db2dae5?pvs=4#aad98608f9ef411faaac226ddcf1ceac)
- fail-safe for ResourceLocation wrapping and recipe id, this should fix recipe builder with invalid id failing silently
- (from Rhizo) `JSON.stringify()` can only the first element in an array
- restricted /kubejs stages ... to be singleplayer/op only
- 'effectOnly' param in spawnLightning(...) not working, [(see GitHub commit)](https://github.com/Hellish-Mods/KesseractJS/commit/8e16bd63f64e63c58c43564e621f289ac6923f75)
- Text wrapped from Component not having color and font, [(see GitHub commit)](https://github.com/Hellish-Mods/KesseractJS/commit/b269158f0d5440c6278758e8ae563adf0c8c3e81)
- Data/Assets loading is 4-5x slower than other regular resource loading mods
