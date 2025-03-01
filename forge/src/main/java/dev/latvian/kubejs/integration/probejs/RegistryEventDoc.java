package dev.latvian.kubejs.integration.probejs;

import dev.latvian.kubejs.registry.RegistryEventJS;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.ts.FunctionDeclaration;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ZZZank
 */
public class RegistryEventDoc implements ProbeJSPlugin {

    @Override
    public Set<String> disableEventDumps(ScriptDump dump) {
        if (dump.scriptType == ScriptType.STARTUP) {
            return RegistryInfos.MAP.values()
                .stream()
                .map(r -> r.eventIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        }
        return ProbeJSPlugin.super.disableEventDumps(dump);
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        val events = new ArrayList<FunctionDeclaration>();

        for (val info : RegistryInfos.MAP.values()) {
            val eventClass = info.registryEventProvider.get().getClass();
            val eventType = eventClass == RegistryEventJS.class
                ? Types.type(RegistryEventJS.class).withParams(Types.typeMaybeGeneric(info.type))
                : Types.typeMaybeGeneric(eventClass);
            for (val eventId : info.eventIds) {
                val statement = Statements.func("onEvent")
                    .param("id", Types.literal(eventId))
                    .param("handler", Types.lambda().param("event", eventType).build())
                    .build();
                events.add(statement);
            }
        }

        scriptDump.addGlobal("kessjs_registry_events", events.toArray(Code[]::new));
    }
}
