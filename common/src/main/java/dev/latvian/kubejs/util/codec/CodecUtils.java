package dev.latvian.kubejs.util.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import lombok.val;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author ZZZank
 */
public class CodecUtils {
    public static <E> Codec<E> stringResolverCodec(
        Function<E, String> toString,
        Function<String, E> fromString
    ) {
        return Codec.STRING.comapFlatMap(
            str -> Optional.ofNullable(fromString.apply(str))
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error("Unknown element id: " + str)),
            toString
        );
    }

    public static <E extends Enum<E> & StringRepresentable> E byName(Codec<E> codec, String s) {
        return fromJsonOrThrow(new JsonPrimitive(s), codec);
    }

    public static <E> E fromJsonOrThrow(JsonElement json, Codec<E> codec) {
        return fromJsonOrThrow(
            json, codec, str -> {
                throw new JsonSyntaxException("Could not decode element from JSON: " + str);
            }
        );
    }

    public static <E> JsonElement toJsonOrThrow(E value, Codec<E> codec) {
        return toJsonOrThrow(
            value, codec, str -> {
                throw new IllegalArgumentException("Could not encode element to JSON: " + str);
            }
        );
    }

    public static <T, X extends Throwable> T getOrThrow(
        DataResult<T> result,
        Function<String, X> errorCreator
    ) throws X {
        val either = result.get();
        return either.left()
            .orElseThrow(() -> errorCreator.apply(either.right().orElseThrow().message()));
    }

    public static <E, X extends Throwable> E fromJsonOrThrow(
        JsonElement json,
        Codec<E> codec,
        Function<String, X> errorCreator
    ) throws X {
        return getOrThrow(codec.parse(JsonOps.INSTANCE, json), errorCreator);
    }

    public static <E, X extends Throwable> JsonElement toJsonOrThrow(
        E value,
        Codec<E> codec,
        Function<String, X> errorCreator
    ) throws X {
        return getOrThrow(codec.encodeStart(JsonOps.INSTANCE, value), errorCreator);
    }

    public static <T> Codec<List<T>> listOfOrSelf(Codec<T> codec) {
        return listOfOrSelf(codec.listOf(), codec);
    }

    // TODO: Check if this is correct
    public static <T> Codec<List<T>> listOfOrSelf(Codec<List<T>> listCodec, Codec<T> codec) {
        return Codec.either(listCodec, codec).xmap(either -> either.map(Function.identity(), List::of), Either::left);
        // return Codec.withAlternative(listCodec, codec.xmap(List::of, List::getFirst));
    }

    //	static <T> String getUniqueId(T input, Codec<T> codec) {
    //		return StringUtilsWrapper.getUniqueId(input, o -> toJsonOrThrow(o, codec));
    //	}
    //
    //	static JsonElement numberProviderJson(NumberProvider gen) {
    //		return toJsonOrThrow(gen, NumberProviders.CODEC);
    //	}
}
