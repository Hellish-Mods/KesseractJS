package dev.latvian.kubejs.client;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.client.asset.GenerateClientAssetsEventJS;
import dev.latvian.kubejs.client.asset.LangEventJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.util.KubeJSPlugins;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KubeJSClientResourcePack extends KubeJSResourcePack {
	public static List<PackResources> inject(List<PackResources> packs) {
		if (KubeJS.instance == null) {
			return packs;
		}
		val injected = new ArrayList<>(packs);
		//KubeJS injected resources should have lower priority then user resources packs, which means file pack
		int pos = injected.size();
		for (int i = 0; i < pos; i++) {
			if (injected.get(i) instanceof FilePackResources) {
				pos = i;
				break;
			}
		}
		injected.add(pos, new KubeJSClientResourcePack());
		return injected;
	}

	public KubeJSClientResourcePack() {
		super(PackType.CLIENT_RESOURCES);
	}

	@Override
	public void generateJsonFiles(Map<ResourceLocation, JsonElement> map) {
		AssetJsonGenerator generator = new AssetJsonGenerator(map);
		KubeJSPlugins.forEachPlugin(p -> p.generateAssetJsons(generator));

		generateLang(generator);
	}

	private void generateLang(AssetJsonGenerator generator) {
		val langEvent = new LangEventJS();

		for (val builder : RegistryInfos.ALL_BUILDERS) {
			builder.generateLang(langEvent);
		}

		val langMap = new HashMap<String, String>();
		KubeJSPlugins.forEachPlugin(p -> p.generateLang(langMap));

        new GenerateClientAssetsEventJS(generator).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_GENERATE_ASSET);

        /*
		//read lang json and add into lang event
		try (val in = Files.list(KubeJSPaths.ASSETS)) {
			for (val dir : in.filter(Files::isDirectory).collect(Collectors.toList())) {
				val langDir = dir.resolve("lang");
				if (!Files.exists(langDir) || !Files.isDirectory(langDir)) {
					continue;
				}
				val namespace = dir.getFileName().toString();
				for (val path : Files.list(langDir).filter(Files::isRegularFile).filter(Files::isReadable).collect(Collectors.toList())) {
					val fileName = path.getFileName().toString();
					if (!fileName.endsWith(".json")) {
						continue;
					}
					try (val reader = Files.newBufferedReader(path)) {
						val json = JsonUtils.GSON.fromJson(reader, Map.class);
						val lang = fileName.substring(0, fileName.length() - 5);
						langEvent.get(namespace, lang).addAll(json);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
         */

		//using a special namespace to keep backward equivalence
		langEvent.get("kubejs_generated", "en_us").addAll(langMap);
        langEvent.post(ScriptType.CLIENT, KubeJSEvents.CLIENT_LANG);

		for (val lang2entries : langEvent.namespace2lang2entries.values()) {
			for (val entries : lang2entries.values()) {
				generator.json(entries.path(), entries.asJson());
			}
		}
	}
}
