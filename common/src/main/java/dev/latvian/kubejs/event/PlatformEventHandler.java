package dev.latvian.kubejs.event;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.kubejs.script.ScriptManager;

/**
 * @author ZZZank
 */
public abstract class PlatformEventHandler {

    @ExpectPlatform
    public static void onUnload(ScriptManager manager) {}
}
