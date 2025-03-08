package dev.latvian.kubejs.client.toast.icon;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

/**
 * @author ZZZank
 */
public record ItemIcon(ItemStack stack) implements ToastIcon {
    public static final Codec<ItemIcon> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(ItemIcon::stack)
        ).apply(instance, ItemIcon::new)
    );

    @Override
    public void draw(Minecraft mc, PoseStack poseStack, int x, int y, int size) {
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(poseStack.last().pose());
        RenderSystem.enableDepthTest();

        RenderSystem.translated(x - 2D, y + 2D, 0D);
        val s = size / 16F;
        RenderSystem.scalef(s, s, s);

        Lighting.turnBackOn();
        mc.getItemRenderer().renderAndDecorateFakeItem(stack, -8, -8);

        RenderSystem.disableBlend();
        Lighting.turnOff();
        RenderSystem.popMatrix();
    }

    @Override
    public ToastIconType getType() {
        return ToastIconRegistry.ITEM;
    }
}
