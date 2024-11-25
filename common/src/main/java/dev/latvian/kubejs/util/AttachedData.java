package dev.latvian.kubejs.util;

import lombok.Getter;

import java.util.HashMap;

/**
 * @author LatvianModder
 */
@Getter
public class AttachedData extends HashMap<String, Object> {
	private final Object parent;

	public AttachedData(Object p) {
		parent = p;
	}
}