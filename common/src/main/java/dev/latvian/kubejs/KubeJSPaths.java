package dev.latvian.kubejs;

import dev.latvian.kubejs.util.UtilsJS;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.server.packs.PackType;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author LatvianModder
 */
public class KubeJSPaths {
	public static final Path DIRECTORY = Platform.getGameFolder().resolve("kubejs").normalize();
	public static final Path DATA = DIRECTORY.resolve("data");
	public static final Path ASSETS = DIRECTORY.resolve("assets");
	public static final Path STARTUP_SCRIPTS = DIRECTORY.resolve("startup_scripts");
	public static final Path SERVER_SCRIPTS = DIRECTORY.resolve("server_scripts");
	public static final Path CLIENT_SCRIPTS = DIRECTORY.resolve("client_scripts");
	public static final Path CONFIG = DIRECTORY.resolve("config");
	public static final Path EXPORTED = DIRECTORY.resolve("exported");
	public static final Path README = DIRECTORY.resolve("README.txt");

    public static final Path COMMON_PROPERTIES = CONFIG.resolve("common.properties");
    public static final Path CLIENT_PROPERTIES = CONFIG.resolve("client.properties");
    public static final Path CONFIG_DEV_PROPERTIES = CONFIG.resolve("dev.properties");
    public static final Path LOCAL = dir(Platform.getGameFolder().resolve("local").resolve("kubejs"));
    public static final Path LOCAL_CACHE = dir(LOCAL.resolve("cache"));
    public static final Path LOCAL_DEV_PROPERTIES = LOCAL.resolve("dev.properties");
    public static final Path EXPORT = dir(LOCAL.resolve("export"));
    public static final Path EXPORTED_PACKS = dir(LOCAL.resolve("exported_packs"));

    public static final MutableBoolean FIRST_RUN = new MutableBoolean(false);

    static Path dir(Path dir, boolean markFirstRun) {
        if (Files.exists(dir)) {
            return dir;
        }

        try {
            Files.createDirectories(dir);

            if (markFirstRun) {
                FIRST_RUN.setTrue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dir;
    }

    static Path dir(Path dir) {
        return dir(dir, false);
    }

    static {
        createDirIfAbsent(DIRECTORY);
        createDirIfAbsent(CONFIG);
        createDirIfAbsent(EXPORTED);
    }

    private static void createDirIfAbsent(Path directory) {
        if (Files.notExists(directory)) {
			UtilsJS.tryIO(() -> Files.createDirectories(directory));
		}
	}

	public static Path get(PackType type) {
		return type == PackType.CLIENT_RESOURCES ? ASSETS : DATA;
	}

    static Path getLocalDevProperties() {
        return CommonProperties.get().saveDevPropertiesInConfig ? CONFIG_DEV_PROPERTIES : LOCAL_DEV_PROPERTIES;
    }
}
