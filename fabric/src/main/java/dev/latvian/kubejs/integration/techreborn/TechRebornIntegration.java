package dev.latvian.kubejs.integration.techreborn;

import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;

public class TechRebornIntegration implements KubeJSInitializer {

    @Override
    public void onKubeJSInitialization() {
        if (!Platform.isModLoaded("techreborn")) {
            return;
        }
        RegisterRecipeHandlersEvent.EVENT.register(TechRebornIntegration::registerRecipeHandlers);
    }

    private static void registerRecipeHandlers(RegisterRecipeHandlersEvent event) {
        for (val s : new String[]{
            // Default recipes
            "alloy_smelter",
            "assembling_machine",
            "centrifuge",
            "chemical_reactor",
            "compressor",
            "distillation_tower",
            "extractor",
            "grinder",
            "implosion_compressor",
            "industrial_electrolyzer",
            "recycler",
            "scrapbox",
            "vacuum_freezer",
            "solid_canning_machine",
            "wire_mill",
            // Similar enough that the same serializer works
            "blast_furnace",
        }) {
            event.register(new ResourceLocation("techreborn", s), TRRecipeJS::new);
        }

        for (val s : new String[]{
            "industrial_grinder",
            "industrial_grinder",
            "fluid_replicator",
        }) {
            event.register(new ResourceLocation("techreborn", s), TRRecipeWithTankJS::new);
        }

        // To be implemented later
        // "techreborn:fusion_reactor", // See FusionReactorRecipe
        // "techreborn:rolling_machine", // See RollingMachineRecipe
    }
}
