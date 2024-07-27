package dev.latvian.kubejs.script;

import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.event.StartupEventJS;
import dev.latvian.kubejs.util.ClassFilter;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.util.remapper.RemapperManager;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ScriptManager {

	private static final ThreadLocal<Context> CURRENT_CONTEXT = new ThreadLocal<>();

	public final ScriptType type;
	public final Path directory;
	public final String exampleScript;
	public final EventsJS events;
	public final Map<String, ScriptPack> packs;
	private final ClassFilter classFilter;
	public boolean firstLoad;
	private Map<String, Optional<NativeJavaClass>> javaClassCache;
	public Context context;
    public ScriptableObject topScope;

    public ScriptManager(ScriptType t, Path p, String e) {
		type = t;
		directory = p;
		exampleScript = e;
		events = new EventsJS(this);
		packs = new LinkedHashMap<>();
		firstLoad = true;
		classFilter = KubeJSPlugins.createClassFilter(type);
    }

	public void unload() {
		events.clear();
		packs.clear();
		type.errors.clear();
		type.warnings.clear();
		type.console.resetFile();
		javaClassCache = null;
	}

	public void loadFromDirectory() {
		if (Files.notExists(directory)) {
			UtilsJS.tryIO(() -> Files.createDirectories(directory));

			try (InputStream in = KubeJS.class.getResourceAsStream(exampleScript);
				 OutputStream out = Files.newOutputStream(directory.resolve("script.js"))) {
				out.write(IOUtils.toByteArray(in));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		ScriptPack pack = new ScriptPack(this, new ScriptPackInfo(directory.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, directory, "");

		for (ScriptFileInfo fileInfo : pack.info.scripts) {
			ScriptSource.FromPath scriptSource = info -> directory.resolve(info.file);

			Throwable error = fileInfo.preload(scriptSource);

			String packMode = fileInfo.getPackMode();
			if (fileInfo.isIgnored() || (!packMode.equals("default") && !packMode.equals(CommonProperties.get().packMode))) {
				continue;
			}

			if (error == null) {
				pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
			} else {
				KubeJS.LOGGER.error("Failed to pre-load script file {}: {}", fileInfo.location, error);
			}
		}

		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}

	public boolean isClassAllowed(String name) {
		return classFilter.isAllowed(name);
	}

	@Nullable
	public static Context getCurrentContext() {
		return CURRENT_CONTEXT.get();
	}

    public void load() {
        //top level
        this.context = Context.enterWithNewFactory();
        CURRENT_CONTEXT.set(context);
        topScope = context.initStandardObjects();

        //context related
        context.setRemapper(RemapperManager.getDefault());
        context.setClassShutter(this.classFilter);
        context.setApplicationClassLoader(KubeJS.class.getClassLoader());
        context.setCustomProperty("console", type.console);
        context.setCustomProperty("type", type);

        //type wrapper / binding
        TypeWrappers typeWrappers = context.getTypeWrappers();
        var bindingEvent = new BindingsEvent(this, context, topScope);
        BindingsEvent.EVENT.invoker().accept(bindingEvent);

        for (KubeJSPlugin plugin : KubeJSPlugins.all()) {
            plugin.addTypeWrappers(type, typeWrappers);
            plugin.addBindings(bindingEvent);
        }

        long startAll = System.currentTimeMillis();

		int loaded = 0;
		int total = 0;
		for (ScriptPack pack : packs.values()) {
			try {
				pack.context = context;
				pack.scope = context.initStandardObjects();
                pack.scope.setParentScope(topScope);

				for (ScriptFile file : pack.scripts) {
					total++;
					long start = System.currentTimeMillis();

					if (file.load()) {
						loaded++;
						type.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000D + " s");
					} else if (file.getError() != null) {
						if (file.getError() instanceof RhinoException) {
							type.console.error("Error loading KubeJS script: " + file.getError().getMessage());
						} else {
							type.console.error("Error loading KubeJS script: " + file.info.location + ": " + file.getError());
							file.getError().printStackTrace();
						}
					}
				}
			} catch (Throwable ex) {
				type.console.error("Failed to read script pack " + pack.info.namespace + ": ", ex);
			}
		}

		type.console.info("Loaded " + loaded + "/" + total + " KubeJS " + type.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s");
        Context.exit();

		events.postToHandlers(KubeJSEvents.LOADED, events.handlers(KubeJSEvents.LOADED), new StartupEventJS());

		if (loaded != total && type == ScriptType.STARTUP) {
			throw new RuntimeException("There were startup script syntax errors! See logs/kubejs/startup.txt for more info");
		}

		firstLoad = false;
	}

	public NativeJavaClass loadJavaClass(Scriptable scope, Object[] args) {
		String name = args[0].toString();

		if (name.isEmpty()) {
			throw Context.reportRuntimeError("Class name can't be empty!");
		}

		if (javaClassCache == null) {
			javaClassCache = new HashMap<>();
		}

		Optional<NativeJavaClass> ch = javaClassCache.get(name);

		if (ch == null) {
			javaClassCache.put(name, Optional.empty());

			try {
				if (!isClassAllowed(name)) {
					throw Context.reportRuntimeError("Class '" + name + "' is not allowed!");
				}

				Class<?> c = Class.forName(name);
				NativeJavaClass njc = new NativeJavaClass(scope, c);
				javaClassCache.put(name, Optional.of(njc));
				return njc;
			} catch (Throwable ex) {
				throw Context.reportRuntimeError("Class '" + name + "' is not allowed!");
			}
		}

		if (ch.isPresent()) {
			return ch.get();
		}

		throw Context.reportRuntimeError("Class '" + name + "' is not allowed!");
	}
}