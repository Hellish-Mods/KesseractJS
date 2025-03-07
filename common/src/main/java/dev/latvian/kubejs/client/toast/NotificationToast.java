package dev.latvian.kubejs.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.client.toast.icon.*;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class NotificationToast implements Toast {

    public static final Map<Integer, BiFunction<Minecraft, String, ToastIcon>> ICONS = new HashMap<>(Map.of(
        1, TextureIcon::new,
        2, ItemIcon::new,
        3, AtlasIcon::of
    ));

    private final NotificationBuilder notification;

    private final long duration;
    private final ToastIcon icon;
    private final List<FormattedCharSequence> text;
    private int width, height;

    private long lastChanged;
    private boolean changed;

    public NotificationToast(Minecraft mc, NotificationBuilder notification) {
        this.notification = notification;
        this.duration = notification.duration.toMillis();

        this.icon = ICONS.containsKey(this.notification.iconType)
            ? ICONS.get(this.notification.iconType).apply(mc, this.notification.icon)
            : null;

        this.text = new ArrayList<>(2);
        this.width = 0;
        this.height = 0;

        if (!TextWrapper.isEmpty(notification.text)) {
            this.text.addAll(mc.font.split(notification.text, 240));
        }

        for (val l : this.text) {
            this.width = Math.max(this.width, mc.font.width(l));
        }

        this.width += 12;

        if (this.icon != null) {
            this.width += 24;
        }

        this.height = Math.max(this.text.size() * 10 + 12, 28);

        if (this.text.isEmpty() && this.icon != null) {
            this.width = 28;
            this.height = 28;
        }

        //this.width = Math.max(160, 30 + Math.max(mc.font.width(component), component2 == null ? 0 : mc.font.width(component2));
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    private void drawRectangle(Matrix4f m, int x0, int y0, int x1, int y1, int r, int g, int b) {
        val tesselator = Tesselator.getInstance();
        val buf = tesselator.getBuilder();
        buf.begin(7, DefaultVertexFormat.POSITION_COLOR);
        buf.vertex(m, x0, y1, 0F).color(r, g, b, 255).endVertex();
        buf.vertex(m, x1, y1, 0F).color(r, g, b, 255).endVertex();
        buf.vertex(m, x1, y0, 0F).color(r, g, b, 255).endVertex();
        buf.vertex(m, x0, y0, 0F).color(r, g, b, 255).endVertex();
        tesselator.end();
    }

    @Override
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toastComponent, long l) {
        if (this.changed) {
            this.lastChanged = l;
            this.changed = false;
        }

        val mc = toastComponent.getMinecraft();

        poseStack.pushPose();
        poseStack.translate(-2D, 2D, 0D);
        val m = poseStack.last().pose();
        val w = width();
        val h = height();

        val oc = notification.outlineColor.getRgbKJS();
        val ocr = FastColor.ARGB32.red(oc);
        val ocg = FastColor.ARGB32.green(oc);
        val ocb = FastColor.ARGB32.blue(oc);

        val bc = notification.borderColor.getRgbKJS();
        val bcr = FastColor.ARGB32.red(bc);
        val bcg = FastColor.ARGB32.green(bc);
        val bcb = FastColor.ARGB32.blue(bc);

        val bgc = notification.backgroundColor.getRgbKJS();
        val bgcr = FastColor.ARGB32.red(bgc);
        val bgcg = FastColor.ARGB32.green(bgc);
        val bgcb = FastColor.ARGB32.blue(bgc);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        drawRectangle(m, 2, 0, w - 2, h, ocr, ocg, ocb);
        drawRectangle(m, 0, 2, w, h - 2, ocr, ocg, ocb);
        drawRectangle(m, 1, 1, w - 1, h - 1, ocr, ocg, ocb);
        drawRectangle(m, 2, 1, w - 2, h - 1, bcr, bcg, bcb);
        drawRectangle(m, 1, 2, w - 1, h - 2, bcr, bcg, bcb);
        drawRectangle(m, 2, 2, w - 2, h - 2, bgcr, bgcg, bgcb);

        if (icon != null) {
            icon.draw(mc, poseStack, 14, h / 2, notification.iconSize);
        }

        val th = icon == null ? 6 : 26;
        val tv = (h - text.size() * 10) / 2 + 1;

        for (var i = 0; i < text.size(); i++) {
            if (notification.textShadow) {
                mc.font.drawShadow(poseStack, text.get(i), th, tv + i * 10, 0xFFFFFF);
            } else {
                mc.font.draw(poseStack, text.get(i), th, tv + i * 10, 0xFFFFFF);
            }
        }

        poseStack.popPose();
        return l - this.lastChanged < duration ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }
}