package dev.latvian.kubejs.client.toast.icon;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.Optional;

/**
 * @author ZZZank
 */
public record AtlasIcon(Optional<ResourceLocation> atlas, ResourceLocation sprite) implements ToastIcon {
    public static final Codec<AtlasIcon> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("altas").forGetter(AtlasIcon::atlas),
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(AtlasIcon::sprite)
        ).apply(instance, AtlasIcon::new)
    );

    public static AtlasIcon of(Minecraft mc, String icon) {
        val s = icon.split("\\|");

        if (s.length == 2) {
            return new AtlasIcon(Optional.of(new ResourceLocation(s[0])), new ResourceLocation(s[1]));
        } else {
            return new AtlasIcon(Optional.empty(), new ResourceLocation(icon));
        }
    }

    @Override
    public void draw(Minecraft mc, PoseStack poseStack, int x, int y, int size) {
        val m = poseStack.last().pose();
        val sprite = mc
            .getTextureAtlas(this.atlas.orElse(InventoryMenu.BLOCK_ATLAS))
            .apply(this.sprite);

        val p0 = -size / 2;
        val p1 = p0 + size;

        val u0 = sprite.getU0();
        val v0 = sprite.getV0();
        val u1 = sprite.getU1();
        val v1 = sprite.getV1();

        val tesselator = Tesselator.getInstance();
        val buf = tesselator.getBuilder();
        buf.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        buf.vertex(m, x + p0, y + p1, 0F).uv(u0, v1).color(255, 255, 255, 255).endVertex();
        buf.vertex(m, x + p1, y + p1, 0F).uv(u1, v1).color(255, 255, 255, 255).endVertex();
        buf.vertex(m, x + p1, y + p0, 0F).uv(u1, v0).color(255, 255, 255, 255).endVertex();
        buf.vertex(m, x + p0, y + p0, 0F).uv(u0, v0).color(255, 255, 255, 255).endVertex();
        tesselator.end();
    }

    @Override
    public ToastIconType getType() {
        return ToastIconRegistry.ATLAS;
    }
}
