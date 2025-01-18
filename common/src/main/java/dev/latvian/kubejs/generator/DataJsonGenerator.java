package dev.latvian.kubejs.generator;

import dev.latvian.kubejs.script.data.GeneratedData;
import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class DataJsonGenerator extends JsonGenerator {
	public DataJsonGenerator(Map<ResourceLocation, GeneratedData> m) {
		super(ConsoleJS.SERVER, m);
	}
}
