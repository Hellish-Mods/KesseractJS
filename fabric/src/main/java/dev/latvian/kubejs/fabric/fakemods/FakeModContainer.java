package dev.latvian.kubejs.fabric.fakemods;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.registry.types.FakeModBuilder;
import dev.latvian.kubejs.util.Lazy;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.metadata.ModOriginImpl;

public class FakeModContainer implements ModContainer {
    public static final Lazy<ModContainer> KUBEJS_MOD = Lazy.of(FabricLoaderImpl.INSTANCE.getModContainer(KubeJS.MOD_ID)::get);
    public static final Lazy<Path> KUBEJS_ROOT_PATH = Lazy.of(() -> FabricLoaderImpl.INSTANCE
        .getModContainer(KubeJS.MOD_ID)
        .get()
        .getRootPath());

    private final FakeModMetadata metadata;

    public FakeModContainer(FakeModBuilder builder) {
        this.metadata = new FakeModMetadata(builder);
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }

    @Override
    public List<Path> getRootPaths() {
        return Collections.singletonList(KUBEJS_ROOT_PATH.get());
    }

    @Override
    public ModOrigin getOrigin() {
        return new ModOriginImpl();
    }

    @Override
    public Optional<ModContainer> getContainingMod() {
        return Optional.of(KUBEJS_MOD.get());
    }

    @Override
    public Collection<ModContainer> getContainedMods() {
        return Collections.emptyList();
    }

    @Override
    public Path getRootPath() {
        return KUBEJS_ROOT_PATH.get();
    }

    @Override
    public Path getPath(String file) {
        return null;
    }
}
