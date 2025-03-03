package dev.latvian.kubejs.script;

import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import me.shedaniel.architectury.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class RegistryTypeWrapperFactory<T> implements TypeWrapperFactory<T> {
	private static List<RegistryTypeWrapperFactory<?>> all;

	public static List<RegistryTypeWrapperFactory<?>> getAll() {
        if (all == null) {
            all = RegistryInfos.MAP.values()
                .stream()
                .filter(i -> i.autoWrap)
                .map(RegistryTypeWrapperFactory::new)
                .collect(Collectors.toList());
        }
        return all;
    }

	public final Class<T> type;
	public final Registry<T> registry;
	public final String name;

    private RegistryTypeWrapperFactory(RegistryInfo<T> info) {
        this(info.type, info.getArchRegistry(), info.key.location().toString());
    }

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
