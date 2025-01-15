package dev.latvian.kubejs;

import dev.latvian.kubejs.util.KubeJSPlugins;
import lombok.val;

import java.nio.file.Files;
import java.util.Properties;

/**
 * @author LatvianModder
 */
public class CommonProperties {
    private static CommonProperties INSTANCE;

    public static CommonProperties get() {
        if (INSTANCE == null) {
            INSTANCE = new CommonProperties();
        }

        return INSTANCE;
    }

    public static void reload() {
        INSTANCE = new CommonProperties();
    }

    private final Properties properties;
    private boolean writeProperties;

    public boolean hideServerScriptErrors;
    public boolean serverOnly;
    public boolean announceReload;
    public boolean invertClassLoader;
    public String packMode;
    public boolean debugInfo;
    public boolean saveDevPropertiesInConfig;
    public boolean allowAsyncStreams;
    public boolean matchJsonRecipes;
    public boolean ignoreCustomUniqueRecipeIds;
    public boolean startupErrorGUI;
    public String startupErrorReportUrl;

    private CommonProperties() {
        properties = new Properties();

        try {
            writeProperties = false;

            if (Files.exists(KubeJSPaths.COMMON_PROPERTIES)) {
                try (val reader = Files.newBufferedReader(KubeJSPaths.COMMON_PROPERTIES)) {
                    properties.load(reader);
                }
            } else {
                writeProperties = true;
            }

            hideServerScriptErrors = get("hideServerScriptErrors", false);
            serverOnly = get("serverOnly", false);
            announceReload = get("announceReload", true);
            packMode = get("packmode", "");
            saveDevPropertiesInConfig = get("saveDevPropertiesInConfig", false);
            allowAsyncStreams = get("allowAsyncStreams", true);
            matchJsonRecipes = get("matchJsonRecipes", true);
            ignoreCustomUniqueRecipeIds = get("ignoreCustomUniqueRecipeIds", false);
            startupErrorGUI = get("startupErrorGUI", true);
            startupErrorReportUrl = get("startupErrorReportUrl", "");

            KubeJSPlugins.forEachPlugin(p -> p.loadCommonProperties(this));

            if (writeProperties) {
                save();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        KubeJS.LOGGER.info("Loaded common.properties");
    }

    public void remove(String key) {
        val s = properties.getProperty(key);

        if (s != null) {
            properties.remove(key);
            writeProperties = true;
        }
    }

    public String get(String key, String def) {
        val s = properties.getProperty(key);

        if (s == null) {
            properties.setProperty(key, def);
            writeProperties = true;
            return def;
        }

        return s;
    }

    public boolean get(String key, boolean def) {
        return get(key, def ? "true" : "false").equals("true");
    }

    public void save() {
        try (val writer = Files.newBufferedWriter(KubeJSPaths.COMMON_PROPERTIES)) {
            properties.store(writer, "KubeJS Common Properties");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPackMode(String s) {
        packMode = s;
        properties.setProperty("packmode", s);
        save();
    }
}