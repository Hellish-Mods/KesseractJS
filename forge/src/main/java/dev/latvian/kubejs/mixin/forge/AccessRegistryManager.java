package dev.latvian.kubejs.mixin.forge;

import com.google.common.collect.BiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author ZZZank
 */
@Mixin(value = RegistryManager.class, remap = false)
public interface AccessRegistryManager {

    @Accessor("registries")
    BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> kjs$registries();
}
