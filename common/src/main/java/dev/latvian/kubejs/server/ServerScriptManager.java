package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RecipeTypeRegistryEventJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptPackInfo;
import dev.latvian.kubejs.script.ScriptSource;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.DataPackEventJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ServerScriptManager extends ScriptManager {
	public static ServerScriptManager instance;

	@JSInfo("use `ServerScriptManager.instance` directly")
	@Deprecated
	public final ScriptManager scriptManager;

	public ServerScriptManager() {
		super(ScriptType.SERVER, KubeJSPaths.SERVER_SCRIPTS, "/data/kubejs/example_server_script.js");
		scriptManager = this;
	}

	public void init(ServerResources serverResources) {
		try {
			if (Files.notExists(KubeJSPaths.DATA)) {
				Files.createDirectories(KubeJSPaths.DATA);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
		}
		KubeJSReloadListener.resources = serverResources;
	}

	public void reloadScriptManager(ResourceManager resourceManager) {
		unload();
		loadFromDirectory();

		Map<String, List<ResourceLocation>> resPacks = new HashMap<>();
		for (var resource : resourceManager.listResources("kubejs", s -> s.endsWith(".js"))) {
			resPacks.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}

		for (var entry : resPacks.entrySet()) {
			var pack = new ScriptPack(this, new ScriptPackInfo(entry.getKey(), "kubejs/"));

			for (var id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}

			for (var fileInfo : pack.info.scripts) {
				ScriptSource.FromResource scriptSource = info -> resourceManager.getResource(info.id);
				Throwable error = fileInfo.preload(scriptSource);

				if (fileInfo.isIgnored()) {
					continue;
				}

				if (error == null) {
					pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
				} else {
					KubeJS.LOGGER.error("Failed to pre-load script file {}: {}", fileInfo.location, error);
				}
			}

			pack.scripts.sort(null);
			this.packs.put(pack.info.namespace, pack);
		}

		load();
	}

	public List<PackResources> resourcePackList(List<PackResources> original) {
		var virtualDataPackLow = new VirtualKubeJSDataPack(false);
		var virtualDataPackHigh = new VirtualKubeJSDataPack(true);

		List<PackResources> list = new ArrayList<>();
		//10 is expected kjs server resource size, obviously a little bit small
		list.add(virtualDataPackLow);
		list.addAll(original);
		list.add(new KubeJSServerResourcePack());
		list.add(virtualDataPackHigh);

		var resourceManager = new SimpleReloadableResourceManager(PackType.SERVER_DATA);

		for (var resource : list) {
			resourceManager.add(resource);
		}

		reloadScriptManager(resourceManager);

		ConsoleJS.SERVER.setLineNumber(true);
		new DataPackEventJS(virtualDataPackLow).post(ScriptType.SERVER, "server.datapack.last");
		new DataPackEventJS(virtualDataPackLow).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LOW_PRIORITY);
		new DataPackEventJS(virtualDataPackHigh).post(ScriptType.SERVER, "server.datapack.first");
		new DataPackEventJS(virtualDataPackHigh).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_HIGH_PRIORITY);

		UtilsJS.postModificationEvents();

		ConsoleJS.SERVER.setLineNumber(false);
		ConsoleJS.SERVER.info("Scripts loaded");

		Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
		RegisterRecipeHandlersEvent modEvent = new RegisterRecipeHandlersEvent(typeMap);
		KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(modEvent));
		RegisterRecipeHandlersEvent.EVENT.invoker().accept(modEvent);
		new RecipeTypeRegistryEventJS(typeMap).post(ScriptType.SERVER, KubeJSEvents.RECIPES_TYPE_REGISTRY);

		// Currently custom ingredients are only supported on Forge
		if (Platform.isForge()) {
			RecipeEventJS.customIngredientMap = new HashMap<>();
		}

		CustomIngredientAction.MAP.clear();

		RecipeEventJS.instance = new RecipeEventJS(typeMap);
		return list;
	}
}
