package dev.latvian.kubejs.forge;

import dev.latvian.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.kubejs.event.forge.PlatformEventHandlerImpl;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassFilter;
import dev.latvian.kubejs.world.gen.forge.BiomeDictionaryWrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraftforge.common.BiomeDictionary;

public class BuiltinKubeJSForgePlugin extends BuiltinKubeJSPlugin {
	@Override
	public void addClasses(ScriptType type, ClassFilter filter) {
		super.addClasses(type, filter);

		filter.allow("net.minecraftforge"); // Forge
		filter.deny("net.minecraftforge.fml");
		filter.deny("net.minecraftforge.accesstransformer");
		filter.deny("net.minecraftforge.coremod");

		filter.deny("cpw.mods.modlauncher"); // FML
		filter.deny("cpw.mods.gross");
	}

	@Override
	public void addBindings(BindingsEvent event) {
		super.addBindings(event);

		if (event.type == ScriptType.STARTUP) {
            event.addFunction(
                "onForgeEvent",
                PlatformEventHandlerImpl::onEvent,
                null,
                KubeJSForgeEventHandlerWrapper.class
            );
        }

		event.add("BiomeDictionary", BiomeDictionaryWrapper.class);
	}

	@Override
	public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
		super.addTypeWrappers(type, typeWrappers);
		typeWrappers.register(BiomeDictionary.Type.class, BiomeDictionaryWrapper::getBiomeType);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static Object onPlatformEvent(BindingsEvent event, Object[] args) {
        return PlatformEventHandlerImpl.onEvent(args);
	}
}
