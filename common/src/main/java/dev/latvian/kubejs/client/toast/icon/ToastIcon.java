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

    static ToastIcon read(FriendlyByteBuf buf) throws IOException {
        val index = buf.readVarInt();
        val type = ToastIconRegistry.getOrDefault(index, ToastIconRegistry.NONE);
        return buf.readWithCodec(type.codec());
    }

    default void write(FriendlyByteBuf buf) throws IOException {
        buf.writeInt(this.getType().index());
        buf.writeWithCodec(this.getType().codec(), UtilsJS.cast(this));
    }
}
