package dev.latvian.kubejs.registry;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.StartupEventJS;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegistryEventJS<T> extends StartupEventJS {
	private final RegistryInfo<T> registry;
	public final List<BuilderBase<? extends T>> created;

	public RegistryEventJS(RegistryInfo<T> r) {
		this.registry = r;
		this.created = new ArrayList<>();
	}

	public BuilderBase<? extends T> create(String id, String type) {
		val builderType = registry.builderTypes.get(type);
		if (builderType == null) {
			throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
		}

		val builder = builderType.factory().createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)));
		if (builder == null) {
			throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
		}

        registry.addBuilder(builder);
        created.add(builder);
        return builder;
	}

	public BuilderBase<? extends T> create(String id) {
		val builderType = registry.getDefaultType();
		if (builderType == null) {
			throw new IllegalArgumentException("Registry for type '" + registry.key.location() + "' doesn't have any builders registered!");
		}

		val builder = builderType.factory().createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)));
		if (builder == null) {
			throw new IllegalArgumentException("Unknown type '" + builderType.type() + "' for object '" + id + "'!");
		}

        registry.addBuilder(builder);
        created.add(builder);
        return builder;
	}

	@Deprecated
	public CustomBuilderObject custom(String id, Object object) {
		return createCustom(id, () -> object);
	}

	public CustomBuilderObject createCustom(String id, Supplier<Object> object) {
		if (object == null) {
			throw new IllegalArgumentException("Tried to register a null object with id: " + id);
		}
		val rl = UtilsJS.getMCID(KubeJS.appendModId(id));

		val b = new CustomBuilderObject(rl, object, registry);
		registry.addBuilder(b);
		created.add(b);
		return b;
	}
}