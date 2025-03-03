package dev.latvian.kubejs.integration.probejs;

import dev.latvian.kubejs.registry.BuilderType;
import dev.latvian.kubejs.registry.RegistryEventJS;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.ts.FunctionDeclaration;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author ZZZank
 */
public class KessJSRegistryEvents implements ProbeJSPlugin {

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
        val converter = scriptDump.transpiler.typeConverter;
        val events = new ArrayList<FunctionDeclaration>();

        for (val info : RegistryInfos.MAP.values()) {
            val eventClass = info.registryEventProvider.get().getClass();
            var eventType = eventClass == RegistryEventJS.class
                ? Types.type(RegistryEventJS.class).withParams(converter.convertType(info.type))
                : converter.convertType(eventClass);

            if (!info.builderTypes.isEmpty()) {
                val builderTypeFns = new ArrayList<BaseType>(info.builderTypes.size());
                for (val builderType : info.builderTypes.values()) {
                    val fn = Types.lambda()
                        .param("id", Types.STRING)
                        .param("type", Types.literal(builderType.type()))
                        .returnType(converter.convertType(builderType.builderClass()))
                        .build();
                    builderTypeFns.add(fn);
                }
                eventType = eventType.and(Types.object().literalMember("create", Types.and(builderTypeFns)).build());
            }

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

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        if (scriptDump.scriptType != ScriptType.STARTUP) {
            return Collections.emptySet();
        }
        val classes = Collections.<Class<?>>newSetFromMap(new IdentityHashMap<>());
        RegistryInfos.MAP.values()
            .stream()
            .map(r -> r.registryEventProvider)
            .map(Supplier::get)
            .map(e -> e.getClass())
            .forEach(classes::add);
        RegistryInfos.MAP.values()
            .stream()
            .map(r -> r.builderTypes)
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(BuilderType::builderClass)
            .forEach(classes::add);
        return classes;
    }
}
