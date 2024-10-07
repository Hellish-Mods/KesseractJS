package dev.latvian.kubejs.script;

import dev.latvian.kubejs.util.WithAttachedData;
import lombok.Getter;
import me.shedaniel.architectury.ForgeEvent;

/**
 * @author LatvianModder
 */
@Getter
@ForgeEvent
public class AttachDataEvent<T extends WithAttachedData> {
	private final DataType<T> type;
	private final T parent;

	public AttachDataEvent(DataType<T> t, T p) {
		type = t;
		parent = p;
	}

    public void add(String id, Object object) {
		parent.getData().put(id, object);
	}
}