package dev.latvian.kubejs.client.toast;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.kubejs.KubeJSCodecs;
import dev.latvian.kubejs.client.toast.icon.*;
import dev.latvian.kubejs.text.ImmutableComponent;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import lombok.*;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.Optional;

/**
 * @author ZZZank
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true, chain = true)
@Setter
@ToString
@EqualsAndHashCode
public final class NotificationData {
    public static NotificationData ofText(Component title) {
        return new NotificationData(title);
    }

    public static NotificationData ofTitles(Component title, Component subTitle) {
        return new NotificationData(title.copy().append("\n").append(subTitle));
    }

    public static NotificationData ofEmpty() {
        return new NotificationData(ImmutableComponent.EMPTY);
    }

    public static final Component[] NO_TEXT = new Component[0];
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5L);
    public static final Color DEFAULT_BORDER_COLOR = new SimpleColor(0x472954);
    public static final Color DEFAULT_BACKGROUND_COLOR = new SimpleColor(0x241335);

    public static final Codec<NotificationData> CODEC = RecordCodecBuilder.create(
        builder -> builder.group(
            KubeJSCodecs.DURATION.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(NotificationData::duration),
            KubeJSCodecs.COMPONENT.optionalFieldOf("text", null).forGetter(NotificationData::text),
            ToastIcon.CODEC.optionalFieldOf("icon", null).forGetter(NotificationData::icon),
            Codec.INT.optionalFieldOf("iconSize", 16).forGetter(NotificationData::iconSize),
            KubeJSCodecs.COLOR.optionalFieldOf("outlineColor", ColorWrapper.BLACK)
                .forGetter(NotificationData::outlineColor),
            KubeJSCodecs.COLOR.optionalFieldOf("borderColor", DEFAULT_BORDER_COLOR)
                .forGetter(NotificationData::borderColor),
            KubeJSCodecs.COLOR.optionalFieldOf("backgroundColor", DEFAULT_BACKGROUND_COLOR)
                .forGetter(NotificationData::backgroundColor),
            Codec.BOOL.optionalFieldOf("textShadow", true).forGetter(NotificationData::textShadow)
        ).apply(builder, NotificationData::new)
    );

    private Duration duration;
    private Component text;
    private ToastIcon icon;
    private int iconSize;
    private Color outlineColor;
    private Color borderColor;
    private Color backgroundColor;
    private boolean textShadow;

    public NotificationData(Component text) {
        this.duration = DEFAULT_DURATION;
        this.text = text;
        this.icon = NoIcon.INSTANCE;
        this.iconSize = 16;
        this.outlineColor = ColorWrapper.BLACK;
        this.borderColor = DEFAULT_BORDER_COLOR;
        this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
        this.textShadow = true;
    }

    public NotificationData addLine(Component text) {
        if (this.text.getSiblings().isEmpty() && this.text.getContents().isEmpty()) {
            return text(text);
        }
        val toAppend = this.text instanceof MutableComponent mutable
            ? mutable
            : this.text.copy();
        return text(toAppend.append(ImmutableComponent.LINE_BREAK).append(text));
    }

    public NotificationData textureIcon(ResourceLocation textureLocation) {
        return icon(new TextureIcon(textureLocation));
    }

    public NotificationData itemIcon(ItemStack stack) {
        return icon(new ItemIcon(stack));
    }

    public NotificationData atlasIcon(ResourceLocation atlas, ResourceLocation sprite) {
        return icon(new AtlasIcon(Optional.ofNullable(atlas), sprite));
    }

    public NotificationData atlasIcon(ResourceLocation sprite) {
        return atlasIcon(null, sprite);
    }

    public NotificationData noIcon() {
        return icon(NoIcon.INSTANCE);
    }

    @Environment(EnvType.CLIENT)
    public void show() {
        val mc = Minecraft.getInstance();
        mc.getToasts().addToast(new NotificationToast(mc, this));
    }
}
