package dev.latvian.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import lombok.val;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * @see RegistryInfos
 * @author ZZZank
 */
public final class RegistryInfo<T> implements Iterable<BuilderBase<? extends T>>, TypeWrapperFactory<T> {

    @SuppressWarnings("unchecked")
    public static <T> RegistryInfo<T> of(ResourceKey<? extends Registry<?>> key, Class<T> type) {
        return (RegistryInfo<T>) RegistryInfos.MAP.computeIfAbsent(
            key,
            k -> new RegistryInfo<>(UtilsJS.cast(k), type)
        );
	}

    static <T> RegistryInfo<T> of(Registry<?> registry, Class<T> type) {
		return of(registry.key(), type);
	}

    public final ResourceKey<? extends Registry<T>> key;
	public final Class<T> type;
	public final Map<String, BuilderType<T>> builderTypes;
	public final Map<ResourceLocation, BuilderBase<? extends T>> objects;
	public boolean hasDefaultTags = false;
	private BuilderType<T> defaultType;
	public boolean bypassServerOnly;
	public boolean autoWrap;
	private me.shedaniel.architectury.registry.Registry<T> archRegistry;
	public String languageKeyPrefix;
	//used for backward compatibility
	public Supplier<RegistryEventJS<T>> registryEventProvider = () -> new RegistryEventJS<>(this);
    public final List<String> eventIds;

	private RegistryInfo(ResourceKey<? extends Registry<T>> key, Class<T> type) {
		this.key = key;
		this.type = type;
		this.builderTypes = new LinkedHashMap<>();
		this.objects = new LinkedHashMap<>();
		this.bypassServerOnly = false;
		this.autoWrap = type != Codec.class && type != ResourceLocation.class && type != String.class;

        val location = key.location();
        val rawName = "minecraft".equals(location.getNamespace())
            ? location.getPath()
            : location.getNamespace() + '.' + location.getPath();

        this.languageKeyPrefix = rawName.replace('/', '.');
        eventIds = new ArrayList<>(Arrays.asList(rawName + KubeJSEvents.REGISTRY_SUFFIX));
    }

	public RegistryInfo<T> bypassServerOnly() {
		this.bypassServerOnly = true;
		return this;
	}

	public RegistryInfo<T> customRegistryEvent(Supplier<RegistryEventJS<T>> supplier) {
		this.registryEventProvider = supplier;
		return this;
	}

	public RegistryInfo<T> noAutoWrap() {
		this.autoWrap = false;
		return this;
	}

	public RegistryInfo<T> languageKeyPrefix(String prefix) {
		this.languageKeyPrefix = prefix;
		return this;
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory, boolean isDefault) {
		val b = new BuilderType<>(type, builderType, factory);
		builderTypes.put(type, b);

		if (isDefault) {
			if (defaultType != null) {
				ConsoleJS.STARTUP.warnf("Previous default type '%s' for registry '%s' replaced with '%s'!", defaultType.type(), key.location(), type);
			}

			defaultType = b;
		}
		RegistryInfos.WITH_TYPE.put(key, this);
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
		addType(type, builderType, factory, type.equals("basic"));
	}

	public void addBuilder(BuilderBase<? extends T> builder) {
		if (builder == null) {
			throw new IllegalArgumentException("Can't add null builder in registry '" + key.location() + "'!");
		}
		if (CommonProperties.get().debugInfo) {
			ConsoleJS.STARTUP.info("~ " + key.location() + " | " + builder.id);
		}
		if (objects.containsKey(builder.id)) {
			throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '" + key.location() + "'!");
		}

		objects.put(builder.id, builder);
		RegistryInfos.ALL_BUILDERS.add(builder);
	}

	@Nullable
	public BuilderType<T> getDefaultType() {
		if (builderTypes.isEmpty()) {
			return null;
		} else if (defaultType == null) {
			defaultType = builderTypes.values().iterator().next();
		}

		return defaultType;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof RegistryInfo ri && key.equals(ri.key);
	}

	@Override
	public String toString() {
		return key.location().toString();
	}

    @HideFromJS
	public int registerArch() {
		return registerObjects(this.getArchRegistry()::registerSupplied);
	}

    @HideFromJS
	public int registerObjects(RegistryCallback<T> function) {
		if (CommonProperties.get().debugInfo) {
			if (objects.isEmpty()) {
				KubeJS.LOGGER.info("Skipping {} registry", this);
			} else {
				KubeJS.LOGGER.info("Building {} objects of {} registry", objects.size(), this);
			}
		}

		if (objects.isEmpty()) {
			return 0;
		}

		int added = 0;

		for (val builder : this) {
			if (builder.dummyBuilder || (!builder.getRegistryType().bypassServerOnly && CommonProperties.get().serverOnly)) {
				continue;
			}
			function.accept(builder.id, builder::createTransformedObject);

			if (CommonProperties.get().debugInfo) {
				ConsoleJS.STARTUP.info("+ " + this + " | " + builder.id);
			}

			added++;
		}

		if (!objects.isEmpty() && CommonProperties.get().debugInfo) {
			KubeJS.LOGGER.info("Registered {}/{} objects of {}", added, objects.size(), this);
		}

		return added;
	}

	@NotNull
	@Override
	public Iterator<BuilderBase<? extends T>> iterator() {
		return objects.values().iterator();
	}

	@SuppressWarnings({"unchecked"})
	public me.shedaniel.architectury.registry.Registry<T> getArchRegistry() {
		if (archRegistry == null) {
			archRegistry = KubeJSRegistries.genericRegistry((ResourceKey<Registry<T>>) key);
		}
		return archRegistry;
	}

    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
		return getArchRegistry().entrySet();
	}

	public ResourceLocation getId(T value) {
		return getArchRegistry().getId(value);
	}

	public T getValue(ResourceLocation id) {
		return getArchRegistry().get(id);
	}

	public boolean hasValue(ResourceLocation id) {
		return getArchRegistry().contains(id);
	}

	@Override
	public T wrap(Object o) {
		if (o == null) {
			return null;
		} else if (type.isInstance(o)) {
			return (T) o;
		}

		val id = UtilsJS.getMCID(o);
		val value = getValue(id);

		if (value == null) {
            val e = new IllegalArgumentException(String.format("No such element with id %s in registry %s!", id, this));
            ConsoleJS.STARTUP.error("Error while wrapping registry element type!", e);
			throw e;
		}

		return value;
	}

	public void fireRegistryEvent() {
		val event = registryEventProvider.get();
		event.post(ScriptType.STARTUP, eventIds);
		event.created.forEach(BuilderBase::createAdditionalObjects);
	}
}

