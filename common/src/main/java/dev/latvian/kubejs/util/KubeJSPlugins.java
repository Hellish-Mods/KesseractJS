package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public class KubeJSPlugins {
    private static final String PLUGIN_LIST = "kubejs.plugins.txt";
    private static final String CLASSFILTER_LIST = "kubejs.classfilter.txt";

    private static final List<KubeJSPlugin> PLUGINS = new ArrayList<>();
	private static final List<String> GLOBAL_CLASS_FILTER = new ArrayList<>();
	private static final HashSet<String> WALKED_CLASS_NAMES = new HashSet<>();

    static {
        WALKED_CLASS_NAMES.add(""); //enable it to filter out invalid strings
    }

    public static List<KubeJSPlugin> all() {
        return Collections.unmodifiableList(PLUGINS);
    }

	public static void load(String modid, Path path) throws Exception {
		if (Files.isDirectory(path)) {
			val pluginPath = path.resolve(PLUGIN_LIST);
			if (Files.exists(pluginPath)) {
                try (val reader = Files.newBufferedReader(pluginPath)) {
                    loadPlugin(modid, reader.lines());
                }
			}

			val classFilterPath = path.resolve(CLASSFILTER_LIST);
			if (Files.exists(classFilterPath)) {
				GLOBAL_CLASS_FILTER.addAll(Files.readAllLines(classFilterPath));
			}
		} else if (Files.isRegularFile(path)) {
            if (!path.getFileName().toString().endsWith(".jar") && !path.getFileName().toString().endsWith(".zip")) {
                KubeJS.LOGGER.warn("not-jar and not-zip file '{}' passed to plugin loader", path.getFileName());
                return;
            }
            val file = new ZipFile(path.toFile());

            val pluginEntry = file.getEntry(PLUGIN_LIST);
            if (pluginEntry != null) {
                try (val reader = bufferedReaderFromStream(file.getInputStream(pluginEntry))) {
                    loadPlugin(modid, reader.lines());
                }
            }

            val filterEntry = file.getEntry(CLASSFILTER_LIST);
            if (filterEntry != null) {
                try (val reader = bufferedReaderFromStream(file.getInputStream(filterEntry))) {
                    GLOBAL_CLASS_FILTER.addAll(reader.lines().collect(Collectors.toList()));
                }
            }
        }
	}

	private static void loadPlugin(String id, Stream<String> lines) {
        KubeJS.LOGGER.info("Found {} plugin", id);

        val loadedNames = new HashSet<String>();
        lines
            .map(String::trim)
            /**
             * {@link Set#add(Object)}: return {@code true} if this set did NOT already contain the specified element
             * <p>
             * empty string will also be filtered out by {@link WALKED_CLASS_NAMES}, see static initialization
             */
            .filter(WALKED_CLASS_NAMES::add)
            .forEach(className -> {
                try {
                    val c = Class.forName(className);

                    if (KubeJSPlugin.class.isAssignableFrom(c)) {
                        PLUGINS.add((KubeJSPlugin) c.getDeclaredConstructor().newInstance());
                        loadedNames.add(className);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        KubeJS.LOGGER.info("Loaded {} plugins from {}: ", loadedNames.size(), String.join(", ", loadedNames));
	}

	public static ClassFilter createClassFilter(ScriptType type) {
		val filter = new ClassFilter();
		forEachPlugin(plugin -> plugin.addClasses(type, filter));

		for (val s : GLOBAL_CLASS_FILTER) {
			if (s.length() >= 2) {
				if (s.startsWith("+")) {
					filter.allow(s.substring(1));
				} else if (s.startsWith("-")) {
					filter.deny(s.substring(1));
				}
			}
		}

		return filter;
	}

	public static void forEachPlugin(@NotNull Consumer<KubeJSPlugin> callback) {
        Objects.requireNonNull(callback);
        for (val plugin : PLUGINS) {
            callback.accept(plugin);
        }
	}

    private static BufferedReader bufferedReaderFromStream(InputStream in) {
        return new BufferedReader(new InputStreamReader(new BufferedInputStream(in), StandardCharsets.UTF_8));
    }

    public static void initFromMods() {
        val now = System.currentTimeMillis();
        KubeJS.LOGGER.info("Looking for KubeJS plugins...");

        for (val mod : Platform.getMods()) {
            try {
                for (val path : mod.getFilePaths()) {
                    load(mod.getModId(), path);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        KubeJS.LOGGER.info("Done in {} s", (System.currentTimeMillis() - now) / 1000L);
    }
}
