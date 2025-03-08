package dev.latvian.kubejs.client.toast;

import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.client.toast.icon.*;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class NotificationBuilder {
	public static final Component[] NO_TEXT = new Component[0];
	public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5L);
	public static final Color DEFAULT_BORDER_COLOR = new SimpleColor(0x472954);
	public static final Color DEFAULT_BACKGROUND_COLOR = new SimpleColor(0x241335);

	private static final int FLAG_ICON = 1;
	private static final int FLAG_TEXT_SHADOW = FLAG_ICON << 1;
	private static final int FLAG_DURATION = FLAG_TEXT_SHADOW << 1;

    @HideFromJS
	public static NotificationBuilder of(Context cx, Object object) {
		if (object instanceof NotificationBuilder b) {
			return b;
		} else if (object instanceof Map<?, ?> map) {
			return null; // FIXME
		} else if (object instanceof BaseFunction func) {
			val consumer = (Consumer<NotificationBuilder>) NativeJavaObject.createInterfaceAdapter(cx, Consumer.class, func);
			return make(consumer);
		}
        return new NotificationBuilder(TextWrapper.componentOf(object));
    }

	public static NotificationBuilder make(Consumer<NotificationBuilder> consumer) {
		val b = new NotificationBuilder();
		consumer.accept(b);
		return b;
	}

	public Duration duration;
	public Component text;
	public transient int iconType;
	public transient ToastIcon icon;
	public int iconSize;
	public Color outlineColor;
	public Color borderColor;
	public Color backgroundColor;
	public boolean textShadow;

	public NotificationBuilder(Component text) {
		duration = DEFAULT_DURATION;
		this.text = text;
		iconSize = 16;
		outlineColor = SimpleColor.BLACK;
		borderColor = DEFAULT_BORDER_COLOR;
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		textShadow = true;
	}

    public NotificationBuilder() {
        this(new TextComponent(""));
    }

	public NotificationBuilder(FriendlyByteBuf buf) {
		int flags = buf.readVarInt();
		text = buf.readComponent();

		duration = ((flags & FLAG_DURATION) != 0)
            ? Duration.ofMillis(buf.readVarLong())
            : DEFAULT_DURATION;

        icon = ToastIcon.read(buf);
        iconSize = buf.readByte();

		outlineColor = UtilsJS.readColor(buf);
		borderColor = UtilsJS.readColor(buf);
		backgroundColor = UtilsJS.readColor(buf);
		textShadow = (flags & FLAG_TEXT_SHADOW) != 0;
	}

	public void write(FriendlyByteBuf buf) {
		int flags = 0;

		if (iconType != 0) {
			flags |= FLAG_ICON;
		}

		if (textShadow) {
			flags |= FLAG_TEXT_SHADOW;
		}

		if (duration != DEFAULT_DURATION) {
			flags |= FLAG_DURATION;
		}

		buf.writeVarInt(flags);
		buf.writeComponent(text);

		if (duration != DEFAULT_DURATION) {
			buf.writeVarLong(duration.toMillis());
		}

        icon.write(buf);
        buf.writeByte(iconSize);

		UtilsJS.writeColor(buf, outlineColor);
		UtilsJS.writeColor(buf, borderColor);
		UtilsJS.writeColor(buf, backgroundColor);
	}

	public void setTextureIcon(ResourceLocation textureLocation) {
		this.icon = new TextureIcon(textureLocation);
		this.iconType = 1;
	}

	public void setItemIcon(ItemStack stack) {
		icon = new ItemIcon(stack);
	}

    public void setAtlasIcon(ResourceLocation atlas, ResourceLocation sprite) {
        this.icon = new AtlasIcon(Optional.ofNullable(atlas), sprite);
    }

    public void setAtlasIcon(ResourceLocation sprite) {
        setAtlasIcon(null, sprite);
    }

	@Environment(EnvType.CLIENT)
	public void show() {
		val mc = Minecraft.getInstance();
		mc.getToasts().addToast(new NotificationToast(mc, this));
	}
}