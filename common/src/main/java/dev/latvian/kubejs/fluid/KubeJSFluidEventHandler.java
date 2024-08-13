package dev.latvian.kubejs.fluid;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.Contract;

/**
 * @author LatvianModder
 */
public class KubeJSFluidEventHandler {
	public static void init() {
	}

	@ExpectPlatform
    @Contract("_,_ -> _")
	static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		throw new AssertionError();
	}

    @ExpectPlatform
    @Contract("_ -> _")
    static BucketItem buildBucket(FluidBuilder builder) {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Contract("_,_ -> _")
    static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
        throw new AssertionError();
    }
}