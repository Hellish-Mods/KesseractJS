package dev.latvian.kubejs.util.codec;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import lombok.val;

import java.util.Objects;

/**
 * @author ZZZank
 */
public class JsonCodecUtils {
    public static <T> Codec<T> jsonCodec(TypeAdapter<T> adapter) {
        return Codec.of(jsonBasedEncoder(adapter), jsonBasedDecoder(adapter));
    }

    public static <T> Codec<T> jsonCodec(Gson gson, Class<T> type) {
        return jsonCodec(gson.getAdapter(type));
    }

    public static <T> Encoder<T> jsonBasedEncoder(TypeAdapter<T> adapter) {
        Objects.requireNonNull(adapter);
        return new Encoder<>() {
            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
                try {
                    val serialized = adapter.toJsonTree(input);
                    val result = JsonOps.INSTANCE.convertTo(ops, serialized);
                    return DataResult.success(result);
                } catch (Exception e) {
                    return DataResult.error(e.toString());
                }
            }
        };
    }

    public static <T> Encoder<T> jsonBasedEncoder(Gson gson, Class<T> type) {
        return jsonBasedEncoder(gson.getAdapter(type));
    }

    public static <T> Decoder<T> jsonBasedDecoder(TypeAdapter<T> adapter) {
        Objects.requireNonNull(adapter);
        return new Decoder<>() {
            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
                try {
                    val json = ops.convertTo(JsonOps.INSTANCE, input);
                    val deserialized = adapter.fromJsonTree(json);
                    return DataResult.success(Pair.of(deserialized, input));
                } catch (Exception e) {
                    return DataResult.error(e.toString());
                }
            }
        };
    }

    public static <T> Decoder<T> jsonBasedDecoder(Gson gson, Class<T> type) {
        return jsonBasedDecoder(gson.getAdapter(type));
    }
}
