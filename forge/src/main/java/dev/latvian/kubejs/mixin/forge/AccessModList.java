package dev.latvian.kubejs.mixin.forge;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
@Mixin(value = ModList.class, remap = false)
public interface AccessModList {

    @Accessor("indexedMods")
    Map<String, ModContainer> indexedMods();

    @Accessor("mods")
    List<ModContainer> mods();
}
