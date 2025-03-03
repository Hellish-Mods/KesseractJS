package dev.latvian.kubejs.integration.probejs;

import dev.latvian.kubejs.block.BlockTintFunction;
import dev.latvian.kubejs.item.ItemTintFunction;
import dev.latvian.mods.rhino.mod.util.color.Color;
import lombok.val;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

/**
 * @author ZZZank
 */
public class KessJSTypeAssignments implements ProbeJSPlugin {

    @Override
    public void assignType(ScriptDump scriptDump) {
        scriptDump.assignType(BlockTintFunction.class, Types.type(BlockTintFunction.class).asArray());
        scriptDump.assignType(BlockTintFunction.class, Types.type(Color.class));
        for (val s : new String[]{"grass", "foliage", "evergreen_foliage", "birch_foliage", "water", "redstone"}) {
            scriptDump.assignType(BlockTintFunction.class, Types.literal(s));
        }

        scriptDump.assignType(ItemTintFunction.class, Types.type(ItemTintFunction.class).asArray());
        scriptDump.assignType(ItemTintFunction.class, Types.type(Color.class));
        for (val s : new String[]{"block", "potion", "map", "display_color_nbt"}) {
            scriptDump.assignType(ItemTintFunction.class, Types.literal(s));
        }
    }
}
