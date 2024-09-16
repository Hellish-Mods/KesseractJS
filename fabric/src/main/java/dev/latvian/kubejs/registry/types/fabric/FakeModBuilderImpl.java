package dev.latvian.kubejs.registry.types.fabric;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import dev.latvian.kubejs.fabric.fakemods.FakeModContainer;
import dev.latvian.kubejs.registry.types.FakeModBuilder;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.FabricLoaderImpl;

/**
 * @author G_cat101
 */
public class FakeModBuilderImpl {
    public static ArrayList<ModContainer> fakeMods = new ArrayList<>();

    private static Set<String> getModIds() {
        return FabricLoaderImpl.INSTANCE
            .getAllMods()
            .stream()
            .map(ModContainer::getMetadata)
            .map(ModMetadata::getId)
            .collect(Collectors.toSet());
    }

    public static void addFakeMod(FakeModBuilder builder) throws Exception {
        if (getModIds().contains(builder.modId)) {
            throw new IllegalArgumentException("Tried to create a fake mod with id '" + builder.modId + "', but a mod with that id already exists.");
        }

        fakeMods.add(new FakeModContainer(builder));
    }
}
