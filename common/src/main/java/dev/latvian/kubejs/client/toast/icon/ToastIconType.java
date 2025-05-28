package dev.latvian.kubejs.client.toast.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import dev.latvian.kubejs.KubeJS;
import lombok.val;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * @author ZZZank
 */
public interface ToastIconType {
    ResourceKey<Registry<ToastIconType>> REGISTRY_KEY = ResourceKey.createRegistryKey(KubeJS.id("icon_type"));
    WritableRegistry<ToastIconType> REGISTRY = new MappedRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    static <T extends ToastIconType> T register(ResourceLocation id, T type) {
        REGISTRY.register(ResourceKey.create(REGISTRY_KEY, id), type, Lifecycle.stable());
        return type;
    }

    static ToastIconType register(ResourceLocation id, Codec<? extends ToastIcon> codec) {
        return register(id, () -> codec);
    }

    ToastIconType NONE = register(KubeJS.id("none"), NoIcon.CODEC);
    ToastIconType TEXTURE = register(KubeJS.id("texture"), TextureIcon.CODEC);
    ToastIconType ITEM = register(KubeJS.id("item"), ItemIcon.CODEC);
    ToastIconType ATLAS = register(KubeJS.id("atlas"), AtlasIcon.CODEC);

    Codec<? extends ToastIcon> codec();
}
