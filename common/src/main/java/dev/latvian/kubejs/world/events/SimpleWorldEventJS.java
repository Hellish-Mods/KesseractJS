package dev.latvian.kubejs.world.events;

import dev.latvian.kubejs.world.WorldJS;

/**
 * @author LatvianModder
 */
public class SimpleWorldEventJS extends WorldEventJS {
	private final WorldJS world;

	public SimpleWorldEventJS(WorldJS w) {
		world = w;
	}

	@Override
	public WorldJS getWorld() {
		return world;
	}
}