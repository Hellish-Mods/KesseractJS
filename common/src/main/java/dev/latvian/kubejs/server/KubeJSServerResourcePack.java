package dev.latvian.kubejs.server;

import dev.latvian.kubejs.generator.DataJsonGenerator;
import dev.latvian.kubejs.script.data.GeneratedData;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;

public class KubeJSServerResourcePack extends KubeJSResourcePack {
	public KubeJSServerResourcePack() {
		super(PackType.SERVER_DATA);
	}

	@Override
	public void generate(Map<ResourceLocation, GeneratedData> map) {
        val generator = new DataJsonGenerator(map);

        for (val builder : RegistryInfo.ALL_BUILDERS) {
            builder.generateDataJsons(generator);
        }

        KubeJSPlugins.forEachPlugin(p -> p.generateDataJsons(generator));
	}
}
