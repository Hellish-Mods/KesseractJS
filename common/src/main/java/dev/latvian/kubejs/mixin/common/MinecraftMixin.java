package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.client.ClientProperties;
import dev.latvian.kubejs.client.KubeJSClientResourcePack;
import dev.latvian.kubejs.core.MinecraftClientKJS;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftClientKJS {

    @Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
    private void kjs$setWindowTitle(CallbackInfoReturnable<String> ci) {
        val title = ClientProperties.get().title;
        if (!title.isEmpty()) {
            ci.setReturnValue(title);
        }
    }

    @Redirect(
        method = {"reloadResourcePacks", "<init>"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;")
    )
    private List<PackResources> kjs$loadPacks(PackRepository repository) {
        val packs = repository.openAllSelected();
        // only add the resource pack if KubeJS has loaded to prevent crashes on mod loading errors
        if (KubeJS.instance == null) {
            return packs;
        }

        val injected = new ArrayList<PackResources>(packs.size() + 1);
        injected.addAll(packs);
        //KubeJS injected resources should have lower priority then user resources packs, which means file pack
        int injectPos = injected.size();
        for (int i = 0; i < injectPos; i++) {
            if (injected.get(i) instanceof FilePackResources) {
                injectPos = i;
                break;
            }
        }
        injected.add(injectPos, new KubeJSClientResourcePack());
        return injected;
    }
}