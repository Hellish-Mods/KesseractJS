package dev.latvian.kubejs.fluid.fabric;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.fluid.KubeJSFluidEventHandler;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public class KubeJSFluidEventHandlerImpl extends KubeJSFluidEventHandler {
	public FlowingFluid buildFluid0(boolean source, FluidBuilder builder) {
		return null;
	}

    public LiquidBlock buildFluidBlock0(FluidBuilder builder, BlockBehaviour.Properties properties) {
        return new LiquidBlock(builder.stillFluid, properties) {
        };
    }

    public BucketItem buildBucket0(FluidBuilder builder) {
        return new BucketItemJS(builder);
    }

    public static class BucketItemJS extends BucketItem {
        public final FluidBuilder properties;

        public BucketItemJS(FluidBuilder b) {
            super(b.stillFluid, new Properties().stacksTo(1).tab(KubeJS.tab));
            properties = b;
        }
    }
}
