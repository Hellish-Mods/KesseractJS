package dev.latvian.kubejs.bindings;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @author LatvianModder
 */
public class JsonIOWrapper {

    public static JsonElement readJson(String path) throws IOException {
        val read = read(path);
        return read == null ? JsonNull.INSTANCE : read.toJson();
    }

    public static String readString(String path) throws IOException {
        return JsonUtils.toString(readJson(path));
    }

	@Nullable
	public static MapJS read(File file) throws IOException {
		return JsonUtilsJS.read(file);
	}

	@Nullable
	public static MapJS read(String file) throws IOException {
		return JsonUtilsJS.read(file);
	}

	public static void write(File file, Object json) throws IOException {
		JsonUtilsJS.write(file, MapJS.of(json));
	}

	public static void write(String file, Object json) throws IOException {
		JsonUtilsJS.write(file, MapJS.of(json));
	}
}