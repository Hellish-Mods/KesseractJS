package dev.latvian.kubejs.script;

import lombok.val;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author LatvianModder
 */
public class ScriptFile implements Comparable<ScriptFile> {
	public final ScriptPack pack;
	public final ScriptSource source;

	public final ScriptFileInfo info;

	private Throwable error;

	public ScriptFile(ScriptPack p, ScriptFileInfo i, ScriptSource s) {
		pack = p;
		info = i;
		source = s;
	}

	@Nullable
	public Throwable getError() {
		return error;
	}

	public boolean load() {
		error = null;

		try (val stream = source.createStream(info)) {
			val script = new String(IOUtils.toByteArray(new BufferedInputStream(stream)), StandardCharsets.UTF_8);
            pack.context.evaluateString(pack.scope, script, info.location, 1, null);
            return true;
		} catch (Throwable ex) {
			error = ex;
			return false;
		}
	}

	@Override
	public int compareTo(ScriptFile o) {
		return Integer.compare(o.info.getPriority(), info.getPriority());
	}
}