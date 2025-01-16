package dev.latvian.kubejs;

import com.google.common.base.Stopwatch;
import dev.latvian.kubejs.block.events.KubeJSBlockEventHandler;
import dev.latvian.kubejs.client.KubeJSClient;
import dev.latvian.kubejs.entity.KubeJSEntityEventHandler;
import dev.latvian.kubejs.event.StartupEventJS;
import dev.latvian.kubejs.fluid.KubeJSFluidEventHandler;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.events.KubeJSItemEventHandler;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.registry.types.tab.CreativeTabRegistryEventJS;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.ScriptsLoadedEvent;
import dev.latvian.kubejs.server.KubeJSServerEventHandler;
import dev.latvian.kubejs.util.KubeJSBackgroundThread;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.KubeJSWorldEventHandler;
import dev.latvian.kubejs.world.gen.FlatChunkGeneratorKJS;
import lombok.val;
import me.shedaniel.architectury.platform.Mod;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.utils.EnvExecutor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author LatvianModder
 */
public class KubeJS {
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static Mod thisMod;

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static KubeJS instance;

	public static KubeJSCommon PROXY;
	public static boolean nextClientHasClientMod = false;
	/**
	 * KubeJS's own tab will be registered at {@code <init>}
	 */
	public static CreativeModeTab tab = CreativeModeTab.TAB_MISC;

	public static ScriptManager startupScriptManager;
	public static ScriptManager clientScriptManager;

	public KubeJS() throws Throwable {
		instance = this;
        thisMod = Platform.getMod(MOD_ID);
		Locale.setDefault(Locale.US);
		new KubeJSBackgroundThread().start();

		if (Files.notExists(KubeJSPaths.README)) {
			UtilsJS.tryIO(() -> {
                val list = Arrays.asList(
                    "Find more info on the website: https://kubejs.com/",
                    "",
                    "Directory information:",
                    "",
                    "assets - Acts as a resource pack, you can put any client resources in here, like textures, models, etc. Example: assets/kubejs/textures/item/test_item.png",
                    "data - Acts as a datapack, you can put any server resources in here, like loot tables, functions, etc. Example: data/kubejs/loot_tables/blocks/test_block.json",
                    "",
                    "startup_scripts - Scripts that get loaded once during game startup - Used for adding items and other things that can only happen while the game is loading (Can be reloaded with /kubejs reload_startup_scripts, but it may not work!)",
                    "server_scripts - Scripts that get loaded every time server resources reload - Used for modifying recipes, tags, loot tables, and handling server events (Can be reloaded with /reload)",
                    "client_scripts - Scripts that get loaded every time client resources reload - Used for JEI events, tooltips and other client side things (Can be reloaded with F3+T)",
                    "",
                    "config - KubeJS config storage. This is also the only directory that scripts can access other than world directory",
                    "exported - Data dumps like texture atlases end up here",
                    "",
                    "You can find type-specific logs in logs/kubejs/ directory"
                );
                Files.write(KubeJSPaths.README, list);
			});
		}

		PROXY = EnvExecutor.getEnvSpecific(() -> KubeJSClient::new, () -> KubeJSCommon::new);

        {
            val pluginTimer = Stopwatch.createStarted();
            LOGGER.info("Looking for KubeJS plugins...");
            val allMods = new ArrayList<>(Platform.getMods());
            allMods.remove(thisMod);
            allMods.addFirst(thisMod);
            KubeJSPlugins.load(allMods);
            LOGGER.info("Done in {}", pluginTimer.stop());
        }

		startupScriptManager = new ScriptManager(ScriptType.STARTUP, KubeJSPaths.STARTUP_SCRIPTS, "/data/kubejs/example_startup_script.js");
		clientScriptManager = new ScriptManager(ScriptType.CLIENT, KubeJSPaths.CLIENT_SCRIPTS, "/data/kubejs/example_client_script.js");

		val oldStartupFolder = KubeJSPaths.DIRECTORY.resolve("startup");
		if (Files.exists(oldStartupFolder)) {
			UtilsJS.tryIO(() -> Files.move(oldStartupFolder, KubeJSPaths.STARTUP_SCRIPTS));
		}

		KubeJSPlugins.forEachPlugin(KubeJSPlugin::init);

		if (!CommonProperties.get().serverOnly) {
			val tabEvent = new CreativeTabRegistryEventJS();
			tab = tabEvent.create(KubeJS.MOD_ID, () -> ItemStackJS.of(new ItemStack(Items.PURPLE_DYE)));
			tabEvent.post(ScriptType.STARTUP, KubeJSEvents.TAB_REGISTRY);
		}

		startupScriptManager.unload();
		startupScriptManager.loadFromDirectory();
		startupScriptManager.load();

		KubeJSPlugins.forEachPlugin(KubeJSPlugin::initStartup);

		KubeJSOtherEventHandler.init();
		KubeJSWorldEventHandler.init();
		KubeJSPlayerEventHandler.init();
		KubeJSEntityEventHandler.init();
		KubeJSBlockEventHandler.init();
		KubeJSItemEventHandler.init();
		KubeJSFluidEventHandler.init();
		KubeJSServerEventHandler.init();
		KubeJSRecipeEventHandler.init();

		RegistryInfos.MAP.values().forEach(RegistryInfo::registerArch);

		PROXY.init();
	}

	public static void loadScripts(ScriptPack pack, Path dir, String path) {
		if (!path.isEmpty() && !path.endsWith("/")) {
			path += "/";
		}

		val pathPrefix = path;

		UtilsJS.tryIO(() -> Files
				.walk(dir, 10)
				.filter(Files::isRegularFile)
				.map(file -> dir.relativize(file).toString().replace(File.separatorChar, '/'))
				.filter(name -> name.endsWith(".js"))
				.forEach(fileName -> pack.info.scripts.add(new ScriptFileInfo(pack.info, pathPrefix + fileName))));
	}

	public static String appendModId(String id) {
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}

	public static Path getGameDirectory() {
		return Platform.getGameFolder();
	}

	public static Path verifyFilePath(Path path) throws IOException {
		if (!path.normalize().toAbsolutePath().startsWith(getGameDirectory())) {
			throw new IOException("You can't access files outside Minecraft directory!");
		}

		return path;
	}

	public static void verifyFilePath(File file) throws IOException {
		verifyFilePath(file.toPath());
	}

	public void setup() {
		UtilsJS.init();
		KubeJSNet.init();
		new StartupEventJS().post(ScriptType.STARTUP, KubeJSEvents.INIT);
		Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(KubeJS.MOD_ID, "flat"), FlatChunkGeneratorKJS.CODEC);
		//KubeJSRegistries.chunkGenerators().register(new ResourceLocation(KubeJS.MOD_ID, "flat"), () -> FlatChunkGeneratorKJS.CODEC);
	}

	public void loadComplete() {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::afterInit);
		ScriptsLoadedEvent.EVENT.invoker().run();
		new StartupEventJS().post(ScriptType.STARTUP, KubeJSEvents.POSTINIT);
		UtilsJS.postModificationEvents();
	}
}