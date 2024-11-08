package dev.latvian.kubejs.event;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import org.jetbrains.annotations.Contract;

public abstract class PlatformEventHandler {

	@ExpectPlatform
	@Contract(value = " -> _")
	public static PlatformEventHandler instance() {
		throw new AssertionError("Not implemented");
	}

	public abstract void unregister();

    /**
	 * Helper method to throw and log an exception with the stack trace. Rhino seems to not be able to print the full stacktrace.
	 * The user still only gets the basic message inside startup log. The full stacktrace will be logged at `latest.log`
	 */
	protected static void logException(Exception e, String description) {
		ScriptType.STARTUP.console.error(description + ": " + e.getLocalizedMessage());
		for (val ste : e.getStackTrace()) {
			KubeJS.LOGGER.error(ste.toString());
		}
	}
}
