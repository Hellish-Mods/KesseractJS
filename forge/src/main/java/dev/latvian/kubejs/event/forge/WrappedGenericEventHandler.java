package dev.latvian.kubejs.event.forge;

import net.minecraftforge.eventbus.api.GenericEvent;

import java.util.function.Consumer;

/**
 * @author ZZZank
 */
@FunctionalInterface
public interface WrappedGenericEventHandler extends Consumer<GenericEvent> {
}