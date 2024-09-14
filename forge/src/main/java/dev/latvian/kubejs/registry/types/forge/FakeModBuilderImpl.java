package dev.latvian.kubejs.registry.types.forge;

import java.lang.reflect.Field;
import java.util.Map;

import dev.latvian.kubejs.forge.FakeModInfo;
import dev.latvian.kubejs.registry.types.FakeModBuilder;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

/**
 * @author G_cat101
 */
@SuppressWarnings("unchecked")
public class FakeModBuilderImpl {
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
    
    private static Map<String, ModContainer> indexedMods;
    static {
        ModList modList = ModList.get();

        try {
            Field indexedModsField = modList.getClass().getDeclaredField("indexedMods");
            indexedModsField.setAccessible(true);
            indexedMods = (Map<String, ModContainer>)indexedModsField.get(modList);
        } catch (Exception ex) {
			ex.printStackTrace();
		}
    }

    public static void addFakeMod(FakeModBuilder builder) throws Exception {
        if (indexedMods.containsKey(builder.modId)) {
            throw new IllegalArgumentException("Tried to create a fake mod with id '" + builder.modId + "', but a mod with that id already exists.");
        }

        FakeModInfo info = new FakeModInfo(builder);
        FakeModContainer container = new FakeModContainer(info);

        indexedMods.put(builder.modId, container);
    }
}
