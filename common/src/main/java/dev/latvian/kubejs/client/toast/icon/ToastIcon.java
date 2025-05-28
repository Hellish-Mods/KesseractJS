package dev.latvian.kubejs.client.toast.icon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

/**
 * @author ZZZank
 */
public interface ToastIcon {
    Codec<ToastIcon> CODEC = ToastIconType.REGISTRY.dispatch(ToastIcon::getType, ToastIconType::codec);

    void draw(Minecraft mc, PoseStack graphics, int x, int y, int size);

    ToastIconType getType();

    static ToastIcon read(FriendlyByteBuf buf) {
        try {
            return buf.readWithCodec(CODEC);
        } catch (IOException e) {
            return NoIcon.INSTANCE;
        }
    }

    default void write(FriendlyByteBuf buf) {
        try {
            buf.writeWithCodec(CODEC, this);
        } catch (IOException ignored) {
        }
    }
}
