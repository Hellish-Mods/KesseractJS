package dev.latvian.kubejs.event;

import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.RhinoException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class EventsJS {

	public final ScriptManager scriptManager;
	private final Map<String, List<IEventHandler>> map;

	public EventsJS(ScriptManager t) {
		scriptManager = t;
		map = new Object2ObjectOpenHashMap<>();
	}

	public void listen(String id, IEventHandler handler) {
		id = id.replace("yeet", "remove");

		var list = map.get(id);
		if (list == null) {
			list = new ObjectArrayList<>();
			map.put(id, list);
		}

		list.add(handler);
	}

    @NotNull
	public List<IEventHandler> handlers(String id) {
        return map.getOrDefault(id, Collections.emptyList());
	}

	/**
	 * @return true if there's one handler tried to cancel the event, and the event is cancellable
	 */
	public boolean postToHandlers(String id, List<IEventHandler> handlers, EventJS event) {
		if (handlers.isEmpty()) {
			return false;
		}

        val canCancel = event.canCancel();
		for (val handler : handlers) {
			try {
				handler.onEvent(event);

				if (canCancel && event.isCancelled()) {
					return true;
				}
			} catch (RhinoException ex) {
				scriptManager.type.console.error("Error occurred while handling event '" + id + "': " + ex.getMessage());
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		//ScriptManager.instance.currentFile = null;
		return false;
	}

	public void clear() {
		map.clear();
	}
}