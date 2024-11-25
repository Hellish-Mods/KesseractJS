package dev.latvian.kubejs.event.forge;

import dev.latvian.kubejs.event.PlatformEventHandler;
import dev.latvian.kubejs.script.ScriptManager;

/**
 * @author ZZZank
 */
public class PlatformEventHandlerImpl extends PlatformEventHandler {

    public static void onUnload(ScriptManager manager) {
        SidedNativeEvents.byType(manager.type).unload();
    }
}
