package dev.latvian.kubejs.mixin.fabric;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.latvian.kubejs.registry.types.fabric.FakeModBuilderImpl;

@Mixin(FabricLoaderImpl.class)
public class FabricLoaderImplMixin {
    @Shadow(remap = false)
    protected List<ModContainerImpl> mods;

    @Inject(method = "getAllMods", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void getCategory(CallbackInfoReturnable<Collection<ModContainer>> cir) {
        if (FakeModBuilderImpl.fakeMods.isEmpty()) {
            return;
        }
        ArrayList<ModContainer> modsWithFake = new ArrayList<>(mods.size() + FakeModBuilderImpl.fakeMods.size());

        modsWithFake.addAll(mods);
        modsWithFake.addAll(FakeModBuilderImpl.fakeMods);

        cir.setReturnValue(modsWithFake);
    }
}