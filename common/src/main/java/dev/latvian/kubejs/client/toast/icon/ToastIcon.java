package dev.latvian.kubejs.client.toast.icon;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

/**
 * @author ZZZank
 */
public interface ToastIcon {
    void draw(Minecraft mc, PoseStack graphics, int x, int y, int size);

    ToastIconType getType();

    static ToastIcon read(FriendlyByteBuf buf) {
        val index = buf.readVarInt();
        val type = ToastIconRegistry.getOrDefault(index, ToastIconRegistry.NONE);
        try {
            return buf.readWithCodec(type.codec());
        } catch (IOException e) {
            return null;
        }
    }

    default void write(FriendlyByteBuf buf) {
        buf.writeInt(this.getType().index());
        try {
            buf.writeWithCodec(this.getType().codec(), UtilsJS.cast(this));
        } catch (IOException ignored) {
        }
    }
}
