package dev.latvian.kubejs.event.forge;

import dev.latvian.kubejs.forge.mods.DummyEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

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

    private final List<PackedHandler<?>> packedHandlers = new ArrayList<>();
    public final ScriptType type;

    private SidedNativeEvents(ScriptType type) {
        this.type = type;
        BY_TYPE.put(type, this);
    }

    @HideFromJS
    public void unload() {
        for (val packed : packedHandlers) {
            packed.bus.unregister(packed.handler);
        }
        packedHandlers.clear();
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
        val eventType = (Class<Event>) type.get();
        if (!Event.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(String.format("Event class must be a subclass of '%s'", Event.class));
        }
        onEventTyped(priority, receiveCancelled, eventType, handler);
    }

    public <T extends Event> void onEventTyped(
        EventPriority priority,
        boolean receiveCancelled,
        Class<T> eventType,
        Consumer<T> handler
    ) {
        val packed = new PackedHandler<>(DummyEventJS.selectBus(eventType), handler);
        packedHandlers.add(packed);
        packed.bus.addListener(priority, receiveCancelled, eventType, packed.handler);
    }

    public void onGenericEvent(
        final ClassConvertible genericClassFilter,
        final ClassConvertible type,
        final WrappedGenericEventHandler handler
    ) {
        onGenericEvent(genericClassFilter, EventPriority.NORMAL, false, type, handler);
    }

    public void onGenericEvent(
        final ClassConvertible genericClassFilter,
        final EventPriority priority,
        final boolean receiveCancelled,
        final ClassConvertible type,
        final WrappedGenericEventHandler handler
    ) {
        val eventType = (Class<GenericEvent>) type.get();
        if (!GenericEvent.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(String.format("Event class must be a subclass of '%s'", GenericEvent.class));
        }
        onGenericEventTyped(genericClassFilter.get(), priority, receiveCancelled, eventType, handler);
    }

    public <T extends GenericEvent<? extends F>, F> void onGenericEventTyped(
        Class<F> genericClassFilter,
        EventPriority priority,
        boolean receiveCancelled,
        Class<T> eventType,
        Consumer<T> handler
    ) {
        val packed = new PackedHandler<>(DummyEventJS.selectBus(eventType), handler);
        packedHandlers.add(packed);
        packed.bus.addGenericListener(genericClassFilter, priority, receiveCancelled, eventType, packed.handler);
    }

    class PackedHandler<T> {
        public final IEventBus bus;
        public final Consumer<T> handler;

        public PackedHandler(IEventBus bus, Consumer<T> handler) {
            this.bus = bus;
            this.handler = event -> {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    SidedNativeEvents.this.type.console.error("Error when handling native event", e);
                }
            };
        }
    }
}