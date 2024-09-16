package dev.latvian.kubejs.forge;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dev.latvian.kubejs.registry.types.FakeModBuilder;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

public class FakeModInfo implements IModInfo {
    private final FakeModBuilder builder;

    public FakeModInfo(FakeModBuilder builder) {
        this.builder = builder;
    }

    @Override
    public String getModId() {
        return builder.modId;
    }

    @Override
    public String getDisplayName() {
        return builder.displayName;
    }

    @Override
    public String getNamespace() {
        return builder.namespace;
    }

    @Override
    public String getDescription() {
        return builder.description;
    }

    @Override
    public ArtifactVersion getVersion() {
        return new DefaultArtifactVersion(builder.version);
    }

    @Override
    public IModFileInfo getOwningFile() {
        return null;
    }

    @Override
    public List<? extends ModVersion> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getModProperties() {
        return Collections.emptyMap();
    }

    @Override
    public URL getUpdateURL() {
        return null;
    }
}
