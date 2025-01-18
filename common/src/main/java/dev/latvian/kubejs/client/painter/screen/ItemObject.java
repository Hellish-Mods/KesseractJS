package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.mods.rhino.util.unit.BoolUnit;
import dev.latvian.mods.rhino.util.unit.ConstantUnit;
import dev.latvian.mods.rhino.util.unit.FixedUnit;
import dev.latvian.mods.rhino.util.unit.Unit;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

/**
 * @author ZZZank
 */
public class ItemObject extends ScreenPainterObject {
    private static final int DURABILITY_BAR_WIDTH = 13;

    private ItemStack itemStack = ItemStack.EMPTY;
    private Unit overlay = new BoolUnit(ConstantUnit.ONE);
    private String customText = "";
    private Unit rotation = FixedUnit.ZERO;

    public ItemObject() {
        z = FixedUnit.of(100);
    }

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);

        if (properties.hasAny("item")) {
            itemStack = ItemStackJS.of(properties.tag.get("item")).getItemStack();
        }

        overlay = properties.getUnit("overlay", overlay);
        customText = properties.getString("customText", customText);
        rotation = properties.getUnit("rotation", rotation);
    }

    @Override
    public void draw(ScreenPaintEventJS event) {
        if (itemStack.isEmpty()) {
            return;
        }

        val aw = w.get();
        val ah = h.get();
        val ax = event.alignX(x.get(), aw, alignX);
        val ay = event.alignY(y.get(), ah, alignY);
        val az = z.get();

        event.push();
        event.translate(ax, ay, az);

        if (rotation != FixedUnit.ZERO) {
            event.rotateRad(rotation.get());
        }

        event.scale(aw / 16F, ah / 16F, 1F);
        drawItem(event.matrices, itemStack, 0, overlay.getAsBoolean(), customText.isEmpty() ? null : customText);
        event.pop();
    }

    public static void drawItem(PoseStack poseStack, ItemStack stack, int hash, boolean renderOverlay, @Nullable String text) {
        if (stack.isEmpty()) {
            return;
        }

        val mc = Minecraft.getInstance();
        val itemRenderer = mc.getItemRenderer();
        val bakedModel = itemRenderer.getModel(stack, mc.player != null ? mc.player.level : null, mc.player);

        mc.getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
//        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
//        PoseStack modelViewStack = RenderSystem.getModelViewStack();
//        modelViewStack.pushPose();
//        modelViewStack.mulPoseMatrix(poseStack.last().pose());
        // modelViewStack.translate(x, y, 100.0D + this.blitOffset);
//        modelViewStack.scale(1F, -1F, 1F);
//        modelViewStack.scale(16F, 16F, 16F);
//        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        val flatLight = !bakedModel.usesBlockLight();

        if (flatLight) {
            Lighting.setupForFlatItems();
        }

        itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, new PoseStack(), bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY, bakedModel);
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();

        if (flatLight) {
            Lighting.setupFor3DItems();
        }

//        modelViewStack.popPose();
//        RenderSystem.applyModelViewMatrix();

        if (renderOverlay) {
            val t = Tesselator.getInstance();
            val font = mc.font;

            if (stack.getCount() != 1 || text != null) {
                val s = text == null ? String.valueOf(stack.getCount()) : text;
                poseStack.pushPose();
                poseStack.translate(9D - font.width(s), 1D, 20D);
                font.drawInBatch(s, 0F, 0F, 0xFFFFFF, true, poseStack.last().pose(), bufferSource, false, 0, 0xF000F0);
                bufferSource.endBatch();
                poseStack.popPose();
            }

            if (stack.isDamageableItem() && stack.isDamaged()) {
                //TODO: test if this works with modded durability bar
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                int barWidth = Math.round(DURABILITY_BAR_WIDTH - DURABILITY_BAR_WIDTH * damagePercent(stack));
                int barColor = damageColor(stack);
                draw(poseStack, t, -6, 5, DURABILITY_BAR_WIDTH, 2, 0, 0, 0, 255);
                draw(poseStack, t, -6, 5, barWidth, 1, barColor >> 16 & 255, barColor >> 8 & 255, barColor & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            val cooldown = mc.player == null ? 0F : mc.player.getCooldowns().getCooldownPercent(stack.getItem(), mc.getFrameTime());

            if (cooldown > 0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                draw(poseStack, t, -8, Mth.floor(16F * (1F - cooldown)) - 8, 16, Mth.ceil(16F * cooldown), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    private static void draw(PoseStack matrixStack, Tesselator t, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        if (width <= 0 || height <= 0) {
            return;
        }

//        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        val m = matrixStack.last().pose();
        val renderer = t.getBuilder();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderer.vertex(m, x, y, 0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(m, x, y + height, 0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(m, x + width, y + height, 0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(m, x + width, y, 0).color(red, green, blue, alpha).endVertex();
        t.end();
    }

    private static float damagePercent(ItemStack stack) {
        return (float) stack.getDamageValue() / stack.getMaxDamage();
    }

    public static int damageColor(ItemStack stack) {
        return Mth.hsvToRgb(Math.max(0.0F, 1.0F - damagePercent(stack)) / 3.0F, 1.0F, 1.0F);
    }
}