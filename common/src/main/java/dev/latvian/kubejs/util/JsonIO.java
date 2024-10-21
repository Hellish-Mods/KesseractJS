package dev.latvian.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class JsonIO {

    public static JsonElement readJson(Path path) throws IOException {
		if (!Files.isRegularFile(path)) {
			return null;
		}

		try (val fileReader = Files.newBufferedReader(path)) {
			return new JsonParser().parse(fileReader);
		}
	}

	public static String readString(Path path) throws IOException {
		return JsonUtils.toString(readJson(path));
	}

	@Nullable
	public static Map<?, ?> read(Path path) throws IOException {
		return MapJS.of(readJson(path));
	}

	public static void write(Path path, @Nullable JsonObject json) throws IOException {
		if (json == null || json.isJsonNull()) {
			Files.deleteIfExists(path);
			return;
		}

		try (Writer fileWriter = Files.newBufferedWriter(path)) {
			val jsonWriter = new JsonWriter(fileWriter);
			jsonWriter.setIndent("\t");
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			Streams.write(json, jsonWriter);
		}
	}

	public static JsonArray toArray(JsonElement element) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		}

		val a = new JsonArray();
		a.add(element);
		return a;
	}

	public static void writeJsonHash(DataOutputStream stream, @Nullable JsonElement element) throws IOException {
		if (element == null || element.isJsonNull()) {
			stream.writeByte('-');
		} else if (element instanceof JsonArray arr) {
			stream.writeByte('[');
			for (val e : arr) {
				writeJsonHash(stream, e);
			}
		} else if (element instanceof JsonObject obj) {
			stream.writeByte('{');
			for (val e : obj.entrySet()) {
				stream.writeBytes(e.getKey());
				writeJsonHash(stream, e.getValue());
			}
		} else if (element instanceof JsonPrimitive primitive) {
			stream.writeByte('=');
			if (primitive.isBoolean()) {
				stream.writeBoolean(element.getAsBoolean());
			} else if (primitive.isNumber()) {
				stream.writeDouble(element.getAsDouble());
			} else {
				stream.writeBytes(element.getAsString());
			}
		} else {
			stream.writeByte('?');
			stream.writeInt(element.hashCode());
		}
	}

	public static byte[] getJsonHashBytes(JsonElement json) {
		val baos = new ByteArrayOutputStream();
		try {
			writeJsonHash(new DataOutputStream(baos), json);
		} catch (IOException ex) {
			ex.printStackTrace();
			val h = json.hashCode();
			return new byte[]{(byte) (h >> 24), (byte) (h >> 16), (byte) (h >> 8), (byte) h};
		}

		return baos.toByteArray();
	}

//	public static String getJsonHashString(JsonElement json) {
//		try {
//			val messageDigest = Objects.requireNonNull(MessageDigest.getInstance("MD5"));
//			return new BigInteger(HexFormat.of().formatHex(messageDigest.digest(JsonIO.getJsonHashBytes(json))), 16).toString(36);
//		} catch (Exception ex) {
//			return String.format("%08x", json.hashCode());
//		}
//	}
}