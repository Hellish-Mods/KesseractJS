package dev.latvian.kubejs.fabric.fakemods;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dev.latvian.kubejs.registry.types.FakeModBuilder;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.fabricmc.loader.impl.metadata.ContactInformationImpl;
import net.fabricmc.loader.impl.util.version.StringVersion;

public class FakeModMetadata implements ModMetadata {
    private FakeModBuilder builder;

    public FakeModMetadata(FakeModBuilder builder) {
        this.builder = builder;
    }

    @Override
    public String getId() {
        return builder.modId;
    }
    @Override
    public Version getVersion() {
        return new StringVersion(builder.version);
    }
    @Override
    public String getName() {
        return builder.displayName;
    }
    @Override
    public String getDescription() {
        return builder.description;
    }

    @Override
    public String getType() {
        return "fabric";
    }
    @Override
    public Collection<String> getProvides() {
        return Lists.newArrayList();
    }
    @Override
    public ModEnvironment getEnvironment() {
        return ModEnvironment.UNIVERSAL;
    }
    @Override
    public Collection<ModDependency> getDependencies() {
        return Lists.newArrayList();
    }

    @Override
    public Collection<Person> getAuthors() {
        return Lists.newArrayList();
    }
    @Override
    public Collection<Person> getContributors() {
        return Lists.newArrayList();
    }
    @Override
    public ContactInformation getContact() {
        return new ContactInformationImpl(Maps.newHashMap());
    }
    @Override
    public Collection<String> getLicense() {
        return Lists.newArrayList();
    }
    @Override
    public Optional<String> getIconPath(int size) {
        return Optional.empty();
    }
    @Override
    public boolean containsCustomValue(String key) {
        return false;
    }
    @Override
    public CustomValue getCustomValue(String key) {
        return null;
    }
    @Override
    public Map<String, CustomValue> getCustomValues() {
        return Maps.newHashMap();
    }
    @Override
    public boolean containsCustomElement(String key) {
        return false;
    }
}
