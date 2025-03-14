package dev.latvian.kubejs.forge;

import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.event.forge.ClassConvertible;
import dev.latvian.kubejs.event.forge.SidedNativeEvents;
import dev.latvian.kubejs.event.forge.WrappedEventHandler;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassFilter;
import dev.latvian.kubejs.world.gen.forge.BiomeDictionaryWrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import lombok.val;
import net.minecraftforge.common.BiomeDictionary;

public class BuiltinKubeJSForgePlugin extends KubeJSPlugin {
	@Override
	public void addClasses(ScriptType type, ClassFilter filter) {
		filter.allow("net.minecraftforge"); // Forge
		filter.deny("net.minecraftforge.fml");
		filter.deny("net.minecraftforge.accesstransformer");
		filter.deny("net.minecraftforge.coremod");

		filter.deny("cpw.mods.modlauncher"); // FML
		filter.deny("cpw.mods.gross");
	}

	@Override
	public void addBindings(BindingsEvent event) {
		if (event.type == ScriptType.STARTUP) {
            event.addFunction(
                "onForgeEvent",
                BuiltinKubeJSForgePlugin::onPlatformEvent,
                null,
                WrappedEventHandler.class
            );
        }

		event.add("BiomeDictionary", BiomeDictionaryWrapper.class);
        event.add("NativeEvents", SidedNativeEvents.byType(event.type));
	}

	@Override
	public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
		typeWrappers.register(BiomeDictionary.Type.class, BiomeDictionaryWrapper::getBiomeType);
        typeWrappers.register(ClassConvertible.class, ClassConvertible::of);
	}

	public static Object onPlatformEvent(Object[] args) {
        if (args.length < 2) {
            throw new RuntimeException("Invalid syntax! onPlatformEvent(string, function) required event class and handler");
        }

        try {
            val type = ClassConvertible.of(args[0]);
            val handler = (WrappedEventHandler) args[1];
            SidedNativeEvents.STARTUP.onEvent(type, handler);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    /**
     * @deprecated kept only for backward binary compat
     * @see #onPlatformEvent(Object[])
     */
    @Deprecated
    public static Object onPlatformEvent(BindingsEvent event, Object[] args) {
        return onPlatformEvent(args);
    }
}
