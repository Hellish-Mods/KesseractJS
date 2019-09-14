package dev.latvian.kubejs.script;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class BindingsEvent extends Event
{
	private final Map<String, Object> map;
	private final Map<String, Object> constantMap;

	public BindingsEvent(Map<String, Object> m, Map<String, Object> cm)
	{
		map = m;
		constantMap = cm;
	}

	public void add(String name, Object value)
	{
		map.put(name, value);
	}

	public void addConstant(String name, Object value)
	{
		constantMap.put(name, value);
	}
}