package dev.latvian.kubejs.client.asset;

import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
public record LangEntry(String namespace, String lang, Map<String, String> entries) {
    public LangEntry(String namespace, String lang) {
        this(namespace, lang, new HashMap<>());
    }

    public void add(String key, String text) {
        ensureValid(key, text);
        entries.put(key, text);
    }

    public void addAll(Map<String, String> map) {
        for (var entry : map.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }

    public void addIfAbsent(String key, String text) {
        ensureValid(key, text);
        entries.putIfAbsent(key, text);
    }

    public JsonObject asJson() {
        JsonObject obj = new JsonObject();
        for (var entry : entries.entrySet()) {
            obj.addProperty(entry.getKey(), entry.getValue());
        }
        return obj;
    }

    @JSInfo("`{namespace}:lang/{lang}`")
    public ResourceLocation path() {
        return new ResourceLocation(namespace, "lang/" + lang);
    }

    static void ensureValid(String key, String text) {
        if (key == null || text == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid key or value: [" + key + ", " + text + "]");
        }
    }
}
