package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.client.asset.GenerateClientAssetsEventJS;
import dev.latvian.kubejs.client.asset.LangEventJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.GeneratedData;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class KubeJSClientResourcePack extends KubeJSResourcePack {

	public KubeJSClientResourcePack() {
		super(PackType.CLIENT_RESOURCES);
	}

	@Override
	public void generate(Map<ResourceLocation, GeneratedData> map) {
		val generator = new AssetJsonGenerator(map);

		KubeJSPlugins.forEachPlugin(p -> p.generateAssetJsons(generator));

        new GenerateClientAssetsEventJS(generator).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_GENERATE_ASSET);

		generateLang(generator);
	}

	private void generateLang(AssetJsonGenerator generator) {
		val langEvent = new LangEventJS();

		for (val builder : RegistryInfos.ALL_BUILDERS) {
			builder.generateLang(langEvent);
		}

		Map<String, String> enusLangMap = new HashMap<>();
		KubeJSPlugins.forEachPlugin(p -> p.generateLang(enusLangMap));
        //using a special namespace to keep backward equivalence
        langEvent.get("kubejs_generated", "en_us").addAll(enusLangMap);

		//read lang json and add into lang event
		try (val in = Files.list(KubeJSPaths.ASSETS)) {
			for (val dir : in.filter(Files::isDirectory).toList()) {
				val langDir = dir.resolve("lang");
				if (!Files.exists(langDir) || !Files.isDirectory(langDir)) {
					continue;
				}

				val namespace = dir.getFileName().toString();
				for (val path : Files.list(langDir).filter(Files::isRegularFile).filter(Files::isReadable).toList()) {
					val fileName = path.getFileName().toString();
					if (!fileName.endsWith(".json")) {
						continue;
					}

					try (val reader = Files.newBufferedReader(path)) {
                        val lang = fileName.substring(0, fileName.length() - ".json".length());
						langEvent.get(namespace, lang)
                            .addAll(JsonUtils.GSON.fromJson(reader, Map.class));
					} catch (Exception ex) {
						KubeJS.LOGGER.error(ex);
					}
				}
			}
		} catch (Exception ex) {
			KubeJS.LOGGER.error(ex);
		}

        langEvent.post(ScriptType.CLIENT, KubeJSEvents.CLIENT_LANG);

		for (val lang2entries : langEvent.namespace2lang2entries.values()) {
			for (val entries : lang2entries.values()) {
				generator.json(entries.toPath(), entries.toJson());
			}
		}
	}

    @Override
    protected boolean skipFile(GeneratedData data) {
        return data.id().getPath().startsWith("lang/");
    }
}
