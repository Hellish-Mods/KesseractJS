package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.event.StartupEventJS;

/**
 * @author LatvianModder
 */
public class FluidRegistryEventJS extends StartupEventJS {
	public FluidBuilder create(String name) {
		FluidBuilder builder = new FluidBuilder(name);
		KubeJSObjects.FLUIDS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}
}