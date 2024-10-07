package dev.latvian.kubejs.script;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.registry.types.FakeModBuilder;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;
import me.shedaniel.architectury.platform.Mod;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.utils.Env;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.Contract;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class PlatformWrapper {
	@Getter
    public static class ModInfo {
		private final String id;
		private String name;
		private String version;

		public ModInfo(String i) {
			id = i;
			name = id;
			version = "unknown";
		}

        public void setName(String name) {
			setModName(this, name);
			this.name = name;
		}
    }

	@ExpectPlatform
	@HideFromJS
	@Contract(value = "_,_ -> _")
    static boolean setModName(ModInfo info, String newName) {
		throw new AssertionError("Not Implemented");
	}

	private static final Set<String> MOD_LIST = new LinkedHashSet<>();
	private static final Map<String, ModInfo> MOD_MAP = new LinkedHashMap<>();

	static {
		for (Mod mod : Platform.getMods()) {
			ModInfo info = new ModInfo(mod.getModId());
			info.name = mod.getName();
			info.version = mod.getVersion();
			MOD_LIST.add(info.id);
			MOD_MAP.put(info.id, info);
		}
	}

	public static String getName() {
		return ArchitecturyTarget.getCurrentTarget();
	}

	public static boolean isForge() {
		return Platform.isForge();
	}

	public static boolean isFabric() {
		return Platform.isFabric();
	}

	public static String getMcVersion() {
		return SharedConstants.getCurrentVersion().getName();
	}

	public static Set<String> getList() {
		return MOD_LIST;
	}

	public static String getModVersion() {
		return getInfo(KubeJS.MOD_ID).version;
	}

	public static boolean isLoaded(String modId) {
		return MOD_MAP.containsKey(modId);
	}

	public static ModInfo getInfo(String modID) {
		return MOD_MAP.get(modID);
	}

	public static Map<String, ModInfo> getMods() {
		return MOD_MAP;
	}

	public static FakeModBuilder registerFakeMod(String modId) {
		return new FakeModBuilder(modId);
	}
	public static FakeModBuilder createFakeMod(String modId) {
		return registerFakeMod(modId);
	}

	public static boolean isDevelopmentEnvironment() {
		return Platform.isDevelopmentEnvironment();
	}

	public static boolean isClientEnvironment() {
		return Platform.getEnvironment() == Env.CLIENT;
	}
}