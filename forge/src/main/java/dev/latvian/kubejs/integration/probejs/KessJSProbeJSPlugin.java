package dev.latvian.kubejs.integration.probejs;

import lombok.val;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.*;

/**
 * @author ZZZank
 */
public class KessJSProbeJSPlugin implements ProbeJSPlugin {

    private final List<ProbeJSPlugin> plugins = Arrays.asList(
        new RegistryEventDoc()
    );

    @Override
    public Set<String> disableEventDumps(ScriptDump dump) {
        val ids = new HashSet<String>();
        for (val plugin : plugins) {
            ids.addAll(plugin.disableEventDumps(dump));
        }
        return ids;
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        for (val plugin : plugins) {
            plugin.addGlobals(scriptDump);
        }
    }
}
