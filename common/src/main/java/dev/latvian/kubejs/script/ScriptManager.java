package dev.latvian.kubejs.script;

import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.event.PlatformEventHandler;
import dev.latvian.kubejs.event.StartupEventJS;
import dev.latvian.kubejs.script.prop.ScriptProperty;
import dev.latvian.kubejs.util.ClassFilter;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.util.remapper.RemapperManager;
import lombok.val;
import org.apache.commons.io.IOUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author LatvianModder
 */
public class ScriptManager {

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

    public ScriptManager(ScriptType type, Path path, String examplePath) {
		this.type = type;
		directory = path;
		exampleScript = examplePath;
		events = new EventsJS(this);
		packs = new LinkedHashMap<>();
		firstLoad = true;
		classFilter = KubeJSPlugins.createClassFilter(this.type);
    }

	public void unload() {
        PlatformEventHandler.onUnload(this);

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

			try (val in = KubeJS.class.getResourceAsStream(exampleScript);
                 val out = Files.newOutputStream(directory.resolve("script.js"))) {
                if (in != null) {
                    out.write(IOUtils.toByteArray(in));
                }
            } catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		val pack = new ScriptPack(this, new ScriptPackInfo(directory.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, directory, "");

        val scriptSource = (ScriptSource.FromPath) info -> directory.resolve(info.filePath);
		for (val fileInfo : pack.info.scripts) {
			val error = fileInfo.preload(scriptSource);
            if (error != null) {
                KubeJS.LOGGER.error("Failed to pre-load script file {}: {}", fileInfo.location, error);
                continue;
            }

            if (fileInfo.isIgnored()) {
                continue;
            }

			val packMode = fileInfo.getPackMode();
			if (!packMode.equals(ScriptProperty.PACKMODE.defaultValue)
                && !packMode.equals(CommonProperties.get().packMode)
            ) {
				continue;
			}

            pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
        }

		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}

	public boolean isClassAllowed(String name) {
		return classFilter.isAllowed(name);
	}

    public void load() {
        //top level
        this.context = Context.enterWithNewFactory();
        topScope = context.initStandardObjects();

        //context related
        context.setRemapper(RemapperManager.getDefault());
        context.setClassShutter(this.classFilter);
        context.setApplicationClassLoader(KubeJS.class.getClassLoader());
        context.setCustomProperty("console", type.console);
        context.setCustomProperty("type", type);

        //type wrapper / binding
        val typeWrappers = context.getTypeWrappers();
        val bindingEvent = new BindingsEvent(this, context, topScope);
        BindingsEvent.EVENT.invoker().accept(bindingEvent);

        for (val plugin : KubeJSPlugins.all()) {
            plugin.addTypeWrappers(type, typeWrappers);
            plugin.addBindings(bindingEvent);
        }

        val startAll = System.currentTimeMillis();

		int loaded = 0;
		int total = 0;
		for (val pack : packs.values()) {
			try {
				pack.context = context;
				pack.scope = context.initStandardObjects();
                pack.scope.setParentScope(topScope);

				for (val file : pack.scripts) {
					total++;
					val start = System.currentTimeMillis();

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

		type.console.infof(
            "Loaded %d/%d KubeJS %s scripts in %s s",
            loaded,
            total,
            type.name,
            (System.currentTimeMillis() - startAll) / 1000D
        );
        Context.exit();

		events.postToHandlers(KubeJSEvents.LOADED, events.handlers(KubeJSEvents.LOADED), new StartupEventJS());

		if (loaded != total && type == ScriptType.STARTUP) {
			throw new RuntimeException("There were startup script syntax errors! See logs/kubejs/startup.txt for more info");
		}

		firstLoad = false;
	}

	public NativeJavaClass loadJavaClass(Scriptable scope, Object[] args) {
		val name = args[0].toString();

		if (name.isEmpty()) {
			throw Context.reportRuntimeError("Class name can't be empty!");
		}

		if (javaClassCache == null) {
			javaClassCache = new HashMap<>();
		}

		val ch = javaClassCache.get(name);

		if (ch == null) {
			javaClassCache.put(name, Optional.empty());

			try {
				if (!isClassAllowed(name)) {
					throw Context.reportRuntimeError("Class '" + name + "' is not allowed!");
				}

				val c = Class.forName(name);
				val njc = new NativeJavaClass(scope, c);
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