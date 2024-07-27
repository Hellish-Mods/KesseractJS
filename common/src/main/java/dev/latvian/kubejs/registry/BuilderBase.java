package dev.latvian.kubejs.registry;

import dev.latvian.kubejs.client.asset.LangEventJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.generator.DataJsonGenerator;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public abstract class BuilderBase<T> implements Supplier<T> {
	public final ResourceLocation id;
	protected String translationKey;
	public Component display;
	protected T object;
	public boolean overrideLangJson;
	public transient boolean dummyBuilder;
	public transient Set<ResourceLocation> tags;

	public BuilderBase(ResourceLocation i) {
		id = i;
		object = null;
		translationKey = ""; //compute lazily
		display = null;
		overrideLangJson = false;
		dummyBuilder = false;
		tags = new HashSet<>();
	}

	public abstract RegistryInfo getRegistryType();

	public abstract T createObject();

	public T transformObject(T obj) {
		return obj;
	}

	@Deprecated
	public String getBuilderType() {
		return getRegistryType().key.location().getPath();
	}

	@Override
	public final T get() {
        if (object == null) {
            object = transformObject(createObject());
        }
		return object;
	}

	public void createAdditionalObjects() {
	}

	public String getTranslationKeyGroup() {
		return getRegistryType().languageKeyPrefix;
	}

	@Deprecated
	public void add() {
		ConsoleJS.STARTUP.setLineNumber(true);
		ConsoleJS.STARTUP.log("You no longer need to use .add() at end of " + getBuilderType() + " builder!");
		ConsoleJS.STARTUP.setLineNumber(false);
	}

	@JSInfo("""
		Sets the translation key for this object, e.g. `block.minecraft.stone`.
		""")
	public BuilderBase<T> translationKey(String key) {
		translationKey = key;
		return this;
	}

	@JSInfo("""
		Sets the display name for this object, e.g. `Stone`.

		This will be overridden by a lang file if it exists.

		If you want to make displayName() override language files, see `.overrideLangJson()`.
		""")
	public BuilderBase<T> displayName(Component name) {
		display = name;
		return this;
	}

	@JSInfo("""
		Makes displayName() override language files.
		""")
	public BuilderBase<T> overrideLangJson() {
		overrideLangJson = true;
		return this;
	}

	@JSInfo("""
			Combined method of overrideLangJson().displayName(name).""")
	public BuilderBase<T> displayWithLang(Component name) {
		return overrideLangJson().displayName(name);
	}

	/**
	 * no need for a string variant, we have type wrapper
	 */
	@JSInfo("""
		Adds a tag to this object, e.g. `minecraft:stone`.
		""")
	public BuilderBase<T> tag(ResourceLocation tag) {
		tags.add(tag);
		getRegistryType().hasDefaultTags = true;
		return this;
	}

	public ResourceLocation newID(String prefix, String post) {
		if (prefix.isEmpty() && post.isEmpty()) {
			return id;
		}
		return new ResourceLocation(id.getNamespace(), prefix + id.getPath() + post);
	}

	public void generateDataJsons(DataJsonGenerator generator) {
	}

	public void generateAssetJsons(AssetJsonGenerator generator) {
	}

	public String getTranslationKey() {
		if (translationKey.isEmpty()) {
			return Util.makeDescriptionId(getTranslationKeyGroup(), id);
		}
		return translationKey;
	}

	public void generateLang(LangEventJS event) {
        var name = display == null
            ? UtilsJS.convertSnakeCaseToCamelCase(id.getPath())
            : display.getString();
		event.get(id.getNamespace(), "en_us").add(getTranslationKey(), name);
	}

	@JSInfo("dev only, do not use")
	public T createTransformedObject() {
		object = transformObject(get());
		return object;
	}

	@Override
	public String toString() {
		var n = getClass().getName();
		int i = n.lastIndexOf('.');

		if (i != -1) {
			n = n.substring(i + 1);
		}

		return n + "[" + id + "]";
	}

    @Deprecated
    @JSInfo("use `display` instead")
    public String getDisplayName() {
        return display.getString();
    }

    @Deprecated
    @JSInfo("use `display` instead")
    public void setDisplayName(String displayName) {
        this.display = Component.nullToEmpty(displayName);
    }
}
