package dev.latvian.kubejs.script.prop;

import dev.latvian.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author ZZZank
 */
public final class ScriptProperties {
    private final Int2ObjectMap<Object> internal = new Int2ObjectOpenHashMap<>();

    public <T> void put(ScriptProperty<T> property, T value) {
        if (value != null) {
            internal.put(property.ordinal, value);
        }
    }

    @NotNull
    public <T> Optional<T> get(ScriptProperty<T> property) {
        return Optional.ofNullable(UtilsJS.cast(internal.get(property.ordinal)));
    }

    @NotNull
    public <T> T getOrDefault(ScriptProperty<T> property) {
        val got = UtilsJS.<T>cast(internal.get(property.ordinal));
        return got == null ? property.defaultValue : got;
    }

    @NotNull
    public Map<Integer, Object> getInternal() {
        return Collections.unmodifiableMap(internal);
    }

    public void reload(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (!line.startsWith("//")) {
                break;
            }

            line = line.substring("//".length()).trim();
            val parts = line.split(":", 2);
            if (parts.length < 2) {
                continue;
            }
            val prop = ScriptProperty.get(parts[0].trim());
            if (prop.isPresent()) {
                val value = prop.get().read(parts[1].trim());
                put(UtilsJS.cast(prop.get()), value);
            }
        }
    }
}