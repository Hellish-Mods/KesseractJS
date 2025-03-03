package dev.latvian.kubejs.fabric;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.gen.fabric.WorldgenAddEventJSFabric;
import dev.latvian.kubejs.world.gen.fabric.WorldgenRemoveEventJSFabric;
import lombok.val;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.loader.api.FabricLoader;

public class KubeJSFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
    @Override
    public void onInitialize() {
        try {
            KubeJS.instance = new KubeJS();
            for (val initializer : FabricLoader.getInstance().getEntrypoints("kubejs-init", KubeJSInitializer.class)) {
                initializer.onKubeJSInitialization();
                KubeJS.LOGGER.debug("[KubeJS] Initialized entrypoint {}.", initializer.getClass().getSimpleName());
            }
            RegistryInfos.MAP.values().forEach(RegistryInfo::registerArch);
            KubeJS.instance.setup();

            BiomeModifications.create(KubeJS.id("worldgen_removals"))
                .add(
                    ModificationPhase.REMOVALS,
                    BiomeSelectors.all(),
                    (s, m) -> new WorldgenRemoveEventJSFabric(s, m).post(
                        ScriptType.STARTUP,
                        KubeJSEvents.WORLDGEN_REMOVE
                    )
                );
            BiomeModifications.create(KubeJS.id("worldgen_additions"))
                .add(
                    ModificationPhase.REPLACEMENTS,
                    BiomeSelectors.all(),
                    (s, m) -> new WorldgenAddEventJSFabric(s, m).post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_ADD)
                );
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public void onInitializeClient() {
        KubeJS.instance.loadComplete();
    }

    @Override
    public void onInitializeServer() {
        KubeJS.instance.loadComplete();
    }
}
