package dev.latvian.kubejs.script.prop;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @author ZZZank
 */
public final class ScriptProperty<T> {
    private static final Map<String, ScriptProperty<?>> ALL = new HashMap<>();
    private static int indexCurrent = 0;

    public static final ScriptProperty<Integer> PRIORITY = register("priority", 0, Integer::valueOf);
    public static final ScriptProperty<List<String>> REQUIRE = register(
        "require",
        Collections.emptyList(),
        (s) -> Arrays.stream(s.split(","))
            .map(String::trim)
            .filter((str) -> !str.isEmpty())
            .toList()
    );
    public static final ScriptProperty<Boolean> IGNORED = register("ignored", false, Boolean::valueOf);
    public static final ScriptProperty<List<String>> AFTER = register(
        "after",
        Collections.emptyList(),
        (s) -> Arrays.stream(s.split(","))
            .map(String::trim)
            .filter((str) -> !str.isEmpty())
            .toList()
    );
    public static final ScriptProperty<String> PACKMODE = register("packmode", "default", Function.identity());

    public final String name;
    public final int ordinal;
    public final T defaultValue;
    public final Function<String, @Nullable T> readerUnsafe;

    public static Optional<ScriptProperty<?>> get(String name) {
        return Optional.ofNullable(ALL.get(name));
    }

    public static <T> ScriptProperty<T> register(String name, T defaultValue, Function<String, @Nullable T> reader) {
        if (ALL.containsKey(name)) {
            throw new IllegalArgumentException("script property with name '%s' already exists");
        }
        var prop = new ScriptProperty<>(name, indexCurrent++, defaultValue, reader);
        ALL.put(name, prop);
        return prop;
    }

    private ScriptProperty(String name, int ordinal, T defaultValue, Function<String, @Nullable T> reader) {
        this.name = name;
        this.ordinal = ordinal;
        this.defaultValue = defaultValue;
        this.readerUnsafe = reader;
    }

    public T read(String raw) {
        try {
            return readerUnsafe.apply(raw);
        } catch (Exception e) {
            return null;
        }
    }
}