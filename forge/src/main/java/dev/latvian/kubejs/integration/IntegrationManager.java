package dev.latvian.kubejs.integration;

import dev.latvian.kubejs.integration.gamestages.GameStagesIntegration;
import dev.latvian.kubejs.integration.probejs.KessJSRegistryEvents;
import dev.latvian.kubejs.integration.probejs.KessJSTypeAssignments;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.recipe.silentsmek.SilentsMechanismsRecipes;
import net.minecraftforge.fml.ModList;
import zzzank.probejs.plugin.ProbeJSPlugins;

/**
 * @author LatvianModder
 */
public class IntegrationManager {
    public static void init() {
        if (modLoaded("gamestages")) {
            GameStagesIntegration.init();
        }

        if (modLoaded("silents_mechanisms") && !modLoaded("kubejs_silents_mechanisms")) {
            RegisterRecipeHandlersEvent.EVENT.register(SilentsMechanismsRecipes::registerRecipeHandlers);
        }

        if (modLoaded("probejs")) {
            ProbeJSPlugins.register(new KessJSRegistryEvents(), new KessJSTypeAssignments());
        }
    }

    public static boolean modLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}