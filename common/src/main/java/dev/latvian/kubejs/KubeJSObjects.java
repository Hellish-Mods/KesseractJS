package dev.latvian.kubejs;

import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * @author LatvianModder
 */
@Deprecated
public class KubeJSObjects {
    @Deprecated
	public static final List<BuilderBase<?>> ALL = RegistryInfos.ALL_BUILDERS;
    @Deprecated
	public static final Map<ResourceLocation, ItemBuilder> ITEMS = UtilsJS.cast(Collections.unmodifiableMap(RegistryInfos.ITEM.objects));
    @Deprecated
	public static final Map<ResourceLocation, BlockBuilder> BLOCKS = UtilsJS.cast(Collections.unmodifiableMap(RegistryInfos.BLOCK.objects));
    @Deprecated
	public static final Map<ResourceLocation, FluidBuilder> FLUIDS = UtilsJS.cast(Collections.unmodifiableMap(RegistryInfos.FLUID.objects));

    public static void register() {
//		ALL.clear();
//		ITEMS.clear();
//		BLOCKS.clear();
//		FLUIDS.clear();
	}
}