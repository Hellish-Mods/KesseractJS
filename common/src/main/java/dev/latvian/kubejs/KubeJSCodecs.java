package dev.latvian.kubejs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.kubejs.util.codec.CodecUtils;
import dev.latvian.kubejs.util.codec.JsonCodecUtils;
import dev.latvian.kubejs.util.time.TimeJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColorWithAlpha;
import dev.latvian.mods.rhino.native_java.type.info.ClassTypeInfo;
import dev.latvian.mods.rhino.native_java.type.info.EnumTypeInfo;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import lombok.val;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.time.Duration;

public interface KubeJSCodecs {
	Codec<Character> CHARACTER = Codec.STRING.xmap(str -> str.charAt(0), Object::toString);
	Codec<ResourceLocation> KUBEJS_ID = Codec.STRING.xmap(
        str -> str.indexOf(':') == -1 ? KubeJS.id(str) : new ResourceLocation(str),
        s -> s.getNamespace().equals(KubeJS.MOD_ID) ? s.getPath() : s.toString()
    );

    Codec<Class<?>> ENUM_CLASS = Codec.STRING.comapFlatMap(
        str -> {
            try {
                val c = Class.forName(str);
                if (!c.isEnum()) {
                    return DataResult.error("Class '" + str + "' is not an enum");
                }
                return DataResult.success(c);
            } catch (ClassNotFoundException e) {
                return DataResult.error("Could not find enum class: " + str);
            }
        }, Class::getName
    );

    Codec<EnumTypeInfo> ENUM_TYPE_INFO = ENUM_CLASS.comapFlatMap(
        c -> {
            if (TypeInfo.of(c) instanceof EnumTypeInfo info) {
                return DataResult.success(info);
            } else {
                return DataResult.error("Class " + c.getTypeName() + " is not an enum!");
            }
        }, ClassTypeInfo::asClass
    );

	Codec<ResourceKey<? extends Registry<?>>> REGISTRY_KEY = ResourceLocation.CODEC.xmap(
        ResourceKey::createRegistryKey,
        ResourceKey::location
    );

	Codec<Duration> DURATION = CodecUtils.stringResolverCodec(
        Duration::toString,
        TimeJS::wrapDuration
    );

	Codec<ResourceKey<? extends Registry<?>>> REGISTRY_KEY_CODEC = ResourceLocation.CODEC.xmap(
        ResourceKey::createRegistryKey,
        ResourceKey::location
    );

    Codec<Component> COMPONENT = JsonCodecUtils.jsonCodec(Component.Serializer.GSON, Component.class);

    Codec<Color> COLOR = Codec.INT.xmap(SimpleColorWithAlpha::new, Color::getArgbKJS);
}