package dev.latvian.kubejs.registry.types.fabric;

import java.util.ArrayList;
import dev.latvian.kubejs.fabric.fakemods.FakeModContainer;
import dev.latvian.kubejs.registry.types.FakeModBuilder;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;

/**
 * @author G_cat101
 */
public class FakeModBuilderImpl {
    public static ArrayList<ModContainer> fakeMods = new ArrayList<>();

    private static ArrayList<String> getModIds() {
        ArrayList<String> modIds = new ArrayList<>();
        FabricLoaderImpl.INSTANCE.getAllMods().forEach(m -> {
            modIds.add(m.getMetadata().getId());
        });
        return modIds;
    }

    public static void addFakeMod(FakeModBuilder builder) throws Exception {
        if (getModIds().contains(builder.modId)) {
            throw new IllegalArgumentException("Tried to create a fake mod with id '" + builder.modId + "', but a mod with that id already exists.");
        }

        fakeMods.add(new FakeModContainer(builder));
    }
}
