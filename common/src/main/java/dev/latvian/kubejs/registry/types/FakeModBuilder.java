package dev.latvian.kubejs.registry.types;

import org.jetbrains.annotations.Contract;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.rhino.util.HideFromJS;

/**
 * @author G_cat101
 */
public class FakeModBuilder {
    public String modId;
    public String displayName;
    public String namespace;
    public String description = "";
    public String version = "1.0";

    public FakeModBuilder(String modId) {
        this.modId = modId;
        this.namespace = modId;
        this.displayName = modId;
        
        addFakeMod(this);
    }

    @ExpectPlatform
    @HideFromJS
	@Contract(value = "_ -> null")
    static void addFakeMod(FakeModBuilder builder) {
		throw new AssertionError("Not Implemented");
    }

    public FakeModBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
    public FakeModBuilder namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }
    public FakeModBuilder description(String description) {
        this.description = description;
        return this;
    }
    public FakeModBuilder version(String version) {
        this.version = version;
        return this;
    }
}
