package dev.latvian.kubejs.script;

import dev.latvian.kubejs.script.prop.ScriptProperties;
import dev.latvian.kubejs.script.prop.ScriptProperty;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ScriptFileInfo {
	private static final Pattern FILE_FIXER = Pattern.compile("[^\\w.\\/]");

	public final ScriptPackInfo pack;
	public final String filePath;
	public final ResourceLocation id;
	public final String location;
    private final ScriptProperties properties = new ScriptProperties();

    public ScriptFileInfo(ScriptPackInfo packInfo, String filePath) {
		pack = packInfo;
		this.filePath = filePath;
        val fixedPath = FILE_FIXER.matcher(pack.pathStart + this.filePath)
            .replaceAll("_")
            .toLowerCase(Locale.ROOT);
        id = new ResourceLocation(pack.namespace, fixedPath);
        location = UtilsJS.getID(pack.namespace + ":" + pack.pathStart + this.filePath);
    }

	@Nullable
	public Throwable preload(ScriptSource source) {
        try (val reader = new BufferedReader(new InputStreamReader(
            source.createStream(this),
            StandardCharsets.UTF_8
        ))) {
            properties.reload(reader);
			return null;
		} catch (Throwable ex) {
			return ex;
		}
	}

    public <T> T getProp(ScriptProperty<T> property) {
        return properties.getOrDefault(property);
    }

    public int getPriority() {
		return getProp(ScriptProperty.PRIORITY);
	}

	public boolean isIgnored() {
		return getProp(ScriptProperty.IGNORED);
	}

    public String getPackMode() {
        return getProp(ScriptProperty.PACKMODE);
    }

    public List<String> getRequiredModIds() {
        return getProp(ScriptProperty.REQUIRE);
    }
}