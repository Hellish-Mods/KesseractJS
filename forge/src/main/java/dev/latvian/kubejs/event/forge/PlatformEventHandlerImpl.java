package dev.latvian.kubejs.event.forge;

import dev.latvian.kubejs.event.PlatformEventHandler;
import dev.latvian.kubejs.forge.KubeJSForgeEventHandlerWrapper;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Kit;
import dev.latvian.mods.rhino.NativeJavaClass;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlatformEventHandlerImpl extends PlatformEventHandler {
	private static final PlatformEventHandlerImpl INSTANCE = new PlatformEventHandlerImpl();
	private final List<KubeJSForgeEventHandlerWrapper> listeners = new ArrayList<>();
    private final List<Consumer<GenericEvent<?>>> genericListeners = new ArrayList<>();

	@Override
	public void unregister() {
        for (var listener : this.listeners) {
            MinecraftForge.EVENT_BUS.unregister(listener);
        }
        this.listeners.clear();
        for (var listener : this.genericListeners) {
            MinecraftForge.EVENT_BUS.unregister(listener);
        }
        this.genericListeners.clear();
	}

	/**
	 * @see PlatformEventHandler#instance()
	 */
	public static PlatformEventHandler instance() {
		return INSTANCE;
	}

    private static Class<?> readClass(Object o) {
        if (o instanceof String s) {
            return Kit.classOrNull(s);
        } else if (o instanceof Class<?> c) {
            return c;
        } else if (o instanceof NativeJavaClass njc) {
            return njc.getClassObject();
        }
        return null;
    }

	public static @Nullable Object onEvent(Object[] params) {
		if (params.length < 2) {
			throw new RuntimeException("Invalid syntax! onForgeEvent(string | Class, function) required event class and handler");
		}

        try {
            Class<?> clazz = readClass(params[0]);
            if (clazz == null) {
                throw new RuntimeException("The first parameter must represent a proper class, instead got:" + params[0]);
            }
            var handler = secured((KubeJSForgeEventHandlerWrapper) params[1], clazz);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, UtilsJS.cast(clazz), handler);
            INSTANCE.listeners.add(handler);
        } catch (Exception e) {
            throw new RuntimeException("Error when creating event handler", e);
        }

        return null;
	}

    public static @Nullable Object onGenericEvent(Object[] params) {
        if (params.length < 2) {
            throw new RuntimeException("Invalid syntax! onForgeEvent(string | Class, function) required event class and handler");
        }

        Class<?> clazz = readClass(params[0]);
        if (clazz == null) {
            throw new RuntimeException("The first parameter must represent a proper class, instead got:" + params[0]);
        }

        try {
            Consumer<GenericEvent<?>> handler = (event) -> {
                try {
                    ((Consumer<GenericEvent<?>>) params[1]).accept(event);
                } catch (Exception e) {
                    logException(e, "Error in native generic event listener for " + clazz);
                }
            };
            MinecraftForge.EVENT_BUS.addGenericListener(UtilsJS.cast(clazz), handler);
            INSTANCE.genericListeners.add(handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

	/**
	 * wrap provided event handler with a try-catch that will catch Exception and log them using {@link PlatformEventHandler#logException(Exception, String)}
	 * @param handler event handler
	 * @param eventTarget event type class
	 * @return wrapped handler
	 */
	static KubeJSForgeEventHandlerWrapper secured(KubeJSForgeEventHandlerWrapper handler, Class eventTarget) {
		return event -> {
			try {
				handler.accept(event);
			} catch (Exception ex) {
				logException(ex, "Error in native event listener for " + eventTarget);
			}
		};
	}
}
