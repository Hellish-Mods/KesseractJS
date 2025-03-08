package dev.latvian.kubejs.client.toast.icon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;

/**
 * @author ZZZank
 */
public class NoIcon implements ToastIcon {
    public static final Codec<NoIcon> CODEC = Codec.unit(null);

    @Override
    public void draw(Minecraft mc, PoseStack graphics, int x, int y, int size) {
    }

    @Override
    public ToastIconType getType() {
        return ToastIconRegistry.NONE;
    }
}
