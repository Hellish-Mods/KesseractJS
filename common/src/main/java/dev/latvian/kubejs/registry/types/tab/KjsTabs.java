package dev.latvian.kubejs.registry.types.tab;

import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import lombok.val;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
public abstract class KjsTabs {
    @JSInfo("""
        Registered tabs from Vanilla and KesseractJS.""")
    private static final Map<String, CreativeModeTab> TABS = new HashMap<>();

    static {
        for (val tab : CreativeModeTab.TABS) {
            TABS.put(tab.getRecipeFolderName(), tab);
        }
    }

    public static CreativeModeTab get(String id) {
        return TABS.get(id);
    }

    public static boolean has(String id) {
        return TABS.containsKey(id);
    }

    /**
     * @return last value associated with provided {@code id}, or null if none associated
     */
    static CreativeModeTab put(String id, CreativeModeTab tab) {
        return TABS.put(id, tab);
    }
}
