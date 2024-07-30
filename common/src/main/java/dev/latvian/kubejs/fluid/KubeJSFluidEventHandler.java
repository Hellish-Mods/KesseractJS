package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.Lazy;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * @author LatvianModder
 */
public abstract class KubeJSFluidEventHandler {

    private static KubeJSFluidEventHandler impl;

    public static void init() {
        impl = Lazy.serviceLoader(KubeJSFluidEventHandler.class).get();
	}

    protected abstract FlowingFluid buildFluid0(boolean source, FluidBuilder builder);

    protected abstract BucketItem buildBucket0(FluidBuilder builder);

    protected abstract LiquidBlock buildFluidBlock0(FluidBuilder builder, BlockBehaviour.Properties properties);

	static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		return impl.buildFluid0(source, builder);
	}

    static BucketItem buildBucket(FluidBuilder builder) {
        return impl.buildBucket0(builder);
    }

    static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
        return impl.buildFluidBlock0(builder, properties);
    }
}