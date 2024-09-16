package dev.latvian.kubejs.registry.types.forge;

import dev.latvian.kubejs.forge.FakeModInfo;
import dev.latvian.kubejs.mixin.forge.AccessModList;
import dev.latvian.kubejs.registry.types.FakeModBuilder;
import lombok.val;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

/**
 * @author G_cat101
 */
public abstract class FakeModBuilderImpl extends FakeModBuilder {
    public FakeModBuilderImpl() {
        super(null);
    }

    public static class FakeModContainer extends ModContainer {
        public FakeModContainer(FakeModInfo info) {
            super(info);
        }
    
        @Override
        public boolean matches(Object mod) {
            return false;
        }

        @Override
        public Object getMod() {
            return null;
        }
    }

    public static void addFakeMod(FakeModBuilder builder) throws Exception {
        val access = ((AccessModList) ModList.get());
        if (access.indexedMods().containsKey(builder.modId)) {
            throw new IllegalArgumentException("Tried to create a fake mod with id '" + builder.modId + "', but a mod with that id already exists.");
        }

        val info = new FakeModInfo(builder);
        val container = new FakeModContainer(info);

        access.mods().add(container);
        access.indexedMods().put(builder.modId, container);
    }
}
