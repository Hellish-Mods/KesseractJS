package dev.latvian.kubejs.util;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class JsonUtilsJS {
    @HideFromJS
	public static final Gson GSON = JsonUtils.GSON;

	public static JsonElement copy(@Nullable JsonElement element) {
		return JsonUtils.copy(element);
	}

	public static JsonElement of(@Nullable Object o) {
        if (o instanceof JsonElement element) {
            return element;
        } else if (o instanceof Map || o instanceof CompoundTag) {
            return MapJS.json(o);
        } else if (o instanceof Collection) {
            return ListJS.json(o);
        }

        val e = JsonUtils.of(o);
        return e == JsonNull.INSTANCE ? null : e;
	}

    @Nullable
    public static JsonPrimitive primitiveOf(@Nullable Object o) {
        return JsonUtils.of(o) instanceof JsonPrimitive p ? p : null;
    }

	@Nullable
	public static Object toObject(@Nullable JsonElement json) {
		return JsonUtils.toObject(json);
	}

	public static String toString(JsonElement json) {
		return JsonUtils.toString(json);
	}

	public static String toPrettyString(JsonElement json) {
		return JsonUtils.toPrettyString(json);
	}

	@Nullable
	public static Object toPrimitive(@Nullable JsonElement element) {
		return JsonUtils.toPrimitive(element);
	}

    @JSInfo("""
        use {@code parseRaw} instead""")
    @Deprecated
    public static JsonElement fromString(@Nullable String string) {
        return parseRaw(string);
    }

    @JSInfo("""
        parse json
        
        @return parsed Json element, or `null` if string is not valid Json string""")
    public static JsonElement parseRaw(String string) {
        return JsonUtils.fromString(string);
    }

    @JSInfo("""
        parse Json string and try to unwrap it into Java object""")
    public static Object parse(String string) {
        return UtilsJS.wrap(parseRaw(string), JSObjectType.ANY);
    }

    @HideFromJS
	@Nullable
	public static MapJS read(File file) throws IOException {
		KubeJS.verifyFilePath(file);

		if (!file.exists()) {
			return null;
		}

		try (val fileReader = new FileReader(file);
			 val jsonReader = new JsonReader(fileReader)) {
			jsonReader.setLenient(true);
//			boolean lenient = jsonReader.isLenient();
			val element = Streams.parse(jsonReader);

			if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
				throw new JsonSyntaxException("Did not consume the entire document.");
			}
			return MapJS.of(element);
		}
	}

    @HideFromJS
	public static void write(File file, @Nullable MapJS o) throws IOException {
		KubeJS.verifyFilePath(file);

		if (o == null) {
			file.delete();
			return;
		}

		val json = o.toJson();

		try (val fileWriter = new FileWriter(file);
			 val jsonWriter = new JsonWriter(new BufferedWriter(fileWriter))) {
			jsonWriter.setIndent("\t");
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			Streams.write(json, jsonWriter);
		}
	}

    @HideFromJS
	@Nullable
	public static MapJS read(String file) throws IOException {
		return read(KubeJS.getGameDirectory().resolve(file).toFile());
	}

    @HideFromJS
	public static void write(String file, @Nullable MapJS json) throws IOException {
		write(KubeJS.getGameDirectory().resolve(file).toFile(), json);
	}

	public static JsonArray toArray(JsonElement element) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		}
		val a = new JsonArray();
		a.add(element);
		return a;
	}
}