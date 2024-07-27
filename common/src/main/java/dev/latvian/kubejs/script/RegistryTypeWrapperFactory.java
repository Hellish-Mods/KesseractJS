package dev.latvian.kubejs.script;

import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import me.shedaniel.architectury.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class RegistryTypeWrapperFactory<T> implements TypeWrapperFactory<T> {
	private static List<RegistryTypeWrapperFactory<?>> all;

    public static void register(TypeWrappers wrappers) {
        for (var wrapperFactory : getAll()) {
            try {
                wrappers.register(wrapperFactory.type, UtilsJS.cast(wrapperFactory));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

	public static List<RegistryTypeWrapperFactory<?>> getAll() {
        if (all == null) {
            all = new ArrayList<>(RegistryInfos.MAP.size());
            for (RegistryInfo info : RegistryInfos.MAP.values()) {
                if (info.autoWrap) {
                    all.add(new RegistryTypeWrapperFactory<>(
                        info.objectBaseClass,
                        info.getArchRegistry(),
                        info.key.location().toString()
                    ));
                }
            }
        }
        return all;
    }

	public final Class<T> type;
	public final Registry<T> registry;
	public final String name;

	private RegistryTypeWrapperFactory(Class<T> type, Registry<T> registry, String name) {
		this.type = type;
		this.registry = registry;
		this.name = name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T wrap(Object o) {
		if (o == null) {
			return null;
		} else if (type.isAssignableFrom(o.getClass())) {
			return (T) o;
		}
		return registry.get(UtilsJS.getMCID(o));
	}

	@Override
	public String toString() {
		return "RegistryTypeWrapperFactory{type=" + type.getName() + ", registry=" + name + '}';
	}
}
