package dev.latvian.kubejs.event.forge;

import dev.latvian.kubejs.forge.mods.DummyEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * @author ZZZank
 */
public final class SidedNativeEvents {
    private static final EnumMap<ScriptType, SidedNativeEvents> BY_TYPE = new EnumMap<>(ScriptType.class);

    public static final SidedNativeEvents STARTUP = new SidedNativeEvents(ScriptType.STARTUP);
    public static final SidedNativeEvents SERVER = new SidedNativeEvents(ScriptType.SERVER);
    public static final SidedNativeEvents CLIENT = new SidedNativeEvents(ScriptType.CLIENT);

    public static SidedNativeEvents byType(@Nonnull ScriptType type) {
        return BY_TYPE.get(type);
    }

    private final List<Object> handlers = new ArrayList<>();
    public final ScriptType type;

    private SidedNativeEvents(ScriptType type) {
        this.type = type;
        BY_TYPE.put(type, this);
    }

    @HideFromJS
    public void unload() {
        for (val handler : handlers) {
            MinecraftForge.EVENT_BUS.unregister(handler);
        }
        handlers.clear();
    }

    public void onEvent(final ClassConvertible type, final WrappedEventHandler handler) {
        onEvent(EventPriority.NORMAL, false, type, handler);
    }

    public void onEvent(
        final EventPriority priority,
        final boolean receiveCancelled,
        final ClassConvertible type,
        final WrappedEventHandler handler
    ) {
        final WrappedEventHandler safed = event -> {
            try {
                handler.accept(event);
            } catch (Exception e) {
                this.type.console.error("Error when handling native event", e);
            }
        };
        val eventType = (Class<Event>) type.get();
        if (!Event.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(String.format("Event class must be a subclass of '%s'", Event.class));
        }
        handlers.add(safed);
        DummyEventJS.selectBus(eventType).addListener(
            priority,
            receiveCancelled,
            eventType,
            safed
        );
    }

    public void onGenericEvent(
        final ClassConvertible genericClassFilter,
        final EventPriority priority,
        final boolean receiveCancelled,
        final ClassConvertible type,
        final WrappedGenericEventHandler handler
    ) {
        final WrappedGenericEventHandler safed = event -> {
            try {
                handler.accept(event);
            } catch (Exception e) {
                this.type.console.error("Error when handling native generic event", e);
            }
        };
        val eventType = (Class<GenericEvent>) type.get();
        if (!GenericEvent.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(String.format("Event class must be a subclass of '%s'", GenericEvent.class));
        }
        handlers.add(safed);
        DummyEventJS.selectBus(eventType).addGenericListener(
            genericClassFilter.get(),
            priority,
            receiveCancelled,
            eventType,
            safed
        );
    }

    public void onGenericEvent(
        final ClassConvertible genericClassFilter,
        final ClassConvertible type,
        final WrappedGenericEventHandler handler
    ) {
        onGenericEvent(genericClassFilter, EventPriority.NORMAL, false, type, handler);
    }
}