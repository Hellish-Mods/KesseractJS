package dev.latvian.kubejs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author LatvianModder
 */
public class JsonUtilsJS {
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().create();

	public static JsonElement copy(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return JsonNull.INSTANCE;
		} else if (element instanceof JsonArray) {
			val a = new JsonArray();
			for (val e : (JsonArray) element) {
				a.add(copy(e));
			}
			return a;
		} else if (element instanceof JsonObject) {
			val o = new JsonObject();
			for (val entry : ((JsonObject) element).entrySet()) {
				o.add(entry.getKey(), copy(entry.getValue()));
			}
			return o;
		}

		return element;
	}

	public static JsonElement of(@Nullable Object o) {
		if (o == null) {
			return JsonNull.INSTANCE;
		} else if (o instanceof JsonSerializable) {
			return ((JsonSerializable) o).toJson();
		} else if (o instanceof JsonElement) {
			return (JsonElement) o;
		} else if (o instanceof CharSequence) {
			return new JsonPrimitive(o.toString());
		} else if (o instanceof Boolean) {
			return new JsonPrimitive((Boolean) o);
		} else if (o instanceof Number) {
			return new JsonPrimitive((Number) o);
		} else if (o instanceof Character) {
			return new JsonPrimitive((Character) o);
		}

		return JsonNull.INSTANCE;
	}

	@Nullable
	public static Object toObject(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return null;
		} else if (json.isJsonObject()) {
			val map = new LinkedHashMap<String, Object>();
			val o = json.getAsJsonObject();
			for (val entry : o.entrySet()) {
				map.put(entry.getKey(), toObject(entry.getValue()));
			}
			return map;
		} else if (json.isJsonArray()) {
			val a = json.getAsJsonArray();
			val objects = new ArrayList<>(a.size());

			for (val e : a) {
				objects.add(toObject(e));
			}

			return objects;
		}

		return toPrimitive(json);
	}

	public static String toString(JsonElement json) {
		val writer = new StringWriter();

		try {
			val jsonWriter = new JsonWriter(writer);
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			jsonWriter.setHtmlSafe(false);
			Streams.write(json, jsonWriter);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return writer.toString();
	}

	public static String toPrettyString(JsonElement json) {
		val writer = new StringWriter();

		try {
			val jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("\t");
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			jsonWriter.setHtmlSafe(false);
			Streams.write(json, jsonWriter);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return writer.toString();
	}

	public static JsonElement fromString(@Nullable String string) {
		if (string == null || string.isEmpty() || string.equals("null")) {
			return JsonNull.INSTANCE;
		}

		try {
			val jsonReader = new JsonReader(new StringReader(string));
			jsonReader.setLenient(true);
//			boolean lenient = jsonReader.isLenient();
			val element = Streams.parse(jsonReader);

			if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
				throw new JsonSyntaxException("Did not consume the entire document.");
			}

			return element;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return JsonNull.INSTANCE;
	}

	@Nullable
	public static Object toPrimitive(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return null;
		} else if (element.isJsonPrimitive()) {
			val p = element.getAsJsonPrimitive();

			if (p.isBoolean()) {
				return p.getAsBoolean();
			} else if (p.isNumber()) {
				return p.getAsNumber();
			}

			try {
				Double.parseDouble(p.getAsString());
				return p.getAsNumber();
			} catch (Exception ex) {
				return p.getAsString();
			}
		}

		return null;
	}

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

	@Nullable
	public static MapJS read(String file) throws IOException {
		return read(KubeJS.getGameDirectory().resolve(file).toFile());
	}

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