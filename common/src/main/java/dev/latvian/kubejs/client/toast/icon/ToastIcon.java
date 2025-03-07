package dev.latvian.kubejs.client.toast.icon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

/**
 * @author ZZZank
 */
public interface ToastIcon {
    void draw(Minecraft mc, PoseStack graphics, int x, int y, int size);
}
