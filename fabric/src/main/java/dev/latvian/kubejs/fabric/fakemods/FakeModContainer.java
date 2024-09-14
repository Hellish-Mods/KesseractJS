package dev.latvian.kubejs.fabric.fakemods;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import dev.latvian.kubejs.registry.types.FakeModBuilder;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.fabricmc.loader.impl.metadata.ModOriginImpl;

public class FakeModContainer implements ModContainer {
    private FakeModMetadata metadata;

    public FakeModContainer(FakeModBuilder builder) {
        this.metadata = new FakeModMetadata(builder);
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }

    @Override
    public List<Path> getRootPaths() {
        return Lists.newArrayList();
    }
    @Override
    public ModOrigin getOrigin() {
        return new ModOriginImpl();
    }
    @Override
    public Optional<ModContainer> getContainingMod() {
        return Optional.empty();
    }
    @Override
    public Collection<ModContainer> getContainedMods() {
        return Lists.newArrayList();
    }
    @Override
    public Path getRootPath() {
        return null;
    }
    @Override
    public Path getPath(String file) {
        return null;
    }
}
