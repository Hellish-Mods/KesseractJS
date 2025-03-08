package dev.latvian.kubejs.client.toast.icon;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * @author ZZZank
 */
public record TextureIcon(ResourceLocation texture) implements ToastIcon {
    public static final Codec<TextureIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(TextureIcon::texture)
        ).apply(instance, TextureIcon::new)
    );

    public TextureIcon(Minecraft ignored, String icon) {
        this(new ResourceLocation(icon));
    }

    @Override
    public void draw(Minecraft mc, PoseStack matrixStack, int x, int y, int size) {
        mc.getTextureManager().bind(this.texture);
        int p0 = -size / 2;
        int p1 = p0 + size;

        val tessellator = Tesselator.getInstance();
        val buf = tessellator.getBuilder();
        val matrix4f = matrixStack.last().pose();
        buf.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        buf.vertex(matrix4f, x + p0, y + p1, 0F).uv(0F, 1F).color(255, 255, 255, 255).endVertex();
        buf.vertex(matrix4f, x + p1, y + p1, 0F).uv(1F, 1F).color(255, 255, 255, 255).endVertex();
        buf.vertex(matrix4f, x + p1, y + p0, 0F).uv(1F, 0F).color(255, 255, 255, 255).endVertex();
        buf.vertex(matrix4f, x + p0, y + p0, 0F).uv(0F, 0F).color(255, 255, 255, 255).endVertex();
        tessellator.end();
    }

    @Override
    public ToastIconType getType() {
        return ToastIconRegistry.TEXTURE;
    }
}
