package dev.latvian.kubejs.event;

import dev.latvian.kubejs.script.ScriptType;
import org.jetbrains.annotations.NotNull;

/**
 * @author LatvianModder
 */
public class EventJS {
	private boolean cancelled = false;

	public boolean canCancel() {
		return false;
	}

	public final void cancel() {
		cancelled = true;
	}

	public final boolean isCancelled() {
		return cancelled;
	}

	protected void afterPosted(boolean result) {
	}

	public final boolean post(ScriptType t, @NotNull String id) {
		if (t != ScriptType.STARTUP && post(ScriptType.STARTUP, id) && canCancel()) {
			return true;
		}

		EventsJS e = t.manager.get().events;
		boolean b = e.postToHandlers(id, e.handlers(id), this);
		afterPosted(b);
		return b;
	}

	public final boolean post(ScriptType t, String id, String sub) {
		String id1 = id + '.' + sub;

		if (t != ScriptType.STARTUP) {
			EventsJS e = ScriptType.STARTUP.manager.get().events;
			if ((e.postToHandlers(id1, e.handlers(id1), this) || e.postToHandlers(id, e.handlers(id), this)) && canCancel()) {
				afterPosted(true);
				return true;
			}
		}

		EventsJS e = t.manager.get().events;
		boolean b = e.postToHandlers(id1, e.handlers(id1), this) || e.postToHandlers(id, e.handlers(id), this);
		afterPosted(b);
		return b;
	}
}