package dev.latvian.kubejs.client.toast.icon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;

/**
 * @author ZZZank
 */
public enum NoIcon implements ToastIcon {
    INSTANCE;

    public static final Codec<NoIcon> CODEC = Codec.unit(INSTANCE);

    @Override
    public void draw(Minecraft mc, PoseStack graphics, int x, int y, int size) {
    }

    @Override
    public ToastIconType getType() {
        return ToastIconType.NONE;
    }
}
