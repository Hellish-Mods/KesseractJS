package dev.latvian.kubejs.text;

import lombok.val;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Objects;

/**
 * @author ZZZank
 */
public final class ImmutableComponent implements Component {
    public static final ImmutableComponent LINE_BREAK = new ImmutableComponent("\n");
    public static final ImmutableComponent EMPTY = new ImmutableComponent("");

    private final Style style;
    private final String content;
    private final List<Component> siblings;

    private volatile Language cachedLang;
    private FormattedCharSequence cachedFormatted;

    public ImmutableComponent(Style style, String content, List<Component> siblings) {
        this.style = style;
        this.content = content;
        this.siblings = List.copyOf(siblings);
    }

    public ImmutableComponent(String content) {
        this(Style.EMPTY, content, List.of());
    }

    @Override
    public Style getStyle() {
        return style;
    }

    @Override
    public String getContents() {
        return content;
    }

    @Override
    public List<Component> getSiblings() {
        return siblings;
    }

    @Override
    public MutableComponent plainCopy() {
        return new TextComponent(this.content);
    }

    @Override
    public MutableComponent copy() {
        val copied = plainCopy();
        copied.getSiblings().addAll(this.siblings);
        copied.setStyle(this.style);
        return copied;
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        val language = Language.getInstance();

        if (this.cachedLang != language) {
            synchronized (this) {
                if (this.cachedLang != language) {
                    this.cachedLang = language;
                    this.cachedFormatted = language.getVisualOrder(this);
                }
            }
        }

        return this.cachedFormatted;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ImmutableComponent that
            && Objects.equals(style, that.style)
            && Objects.equals(content, that.content)
            && Objects.equals(siblings, that.siblings);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(style);
        result = 31 * result + Objects.hashCode(content);
        result = 31 * result + Objects.hashCode(siblings);
        return result;
    }
}
