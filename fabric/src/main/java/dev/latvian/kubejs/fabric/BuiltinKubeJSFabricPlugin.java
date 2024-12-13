package dev.latvian.kubejs.fabric;

import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassFilter;

public class BuiltinKubeJSFabricPlugin extends KubeJSPlugin {
	@Override
	public void addClasses(ScriptType type, ClassFilter filter) {
		filter.allow("net.fabricmc");
		filter.deny("net.fabricmc.accesswidener");
		filter.deny("net.fabricmc.devlaunchinjector");
		filter.deny("net.fabricmc.loader");
		filter.deny("net.fabricmc.tinyremapper");

		filter.deny("com.chocohead.mm"); // Manningham Mills
	}
}
