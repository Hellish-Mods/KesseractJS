package dev.latvian.kubejs.fluid.forge;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.fluid.KubeJSFluidEventHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.Nullable;

public class KubeJSFluidEventHandlerImpl extends KubeJSFluidEventHandler {
	public static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		if (source) {
			return new ForgeFlowingFluid.Source(createProperties(builder));
		} else {
			return new ForgeFlowingFluid.Flowing(createProperties(builder));
		}
	}

	public static ForgeFlowingFluid.Properties createProperties(FluidBuilder fluidBuilder) {
		if (fluidBuilder.extraPlatformInfo != null) {
			return (ForgeFlowingFluid.Properties) fluidBuilder.extraPlatformInfo;
		}
        FluidAttributes.Builder builder = FluidAttributes.builder(
                new ResourceLocation(fluidBuilder.stillTexture),
                new ResourceLocation(fluidBuilder.flowingTexture)
            )
            .translationKey(fluidBuilder.getTranslationKey())
            .color(fluidBuilder.color)
            .rarity(fluidBuilder.rarity.rarity)
            .density(fluidBuilder.density)
            .viscosity(fluidBuilder.viscosity)
            .luminosity(fluidBuilder.luminosity)
            .temperature(fluidBuilder.temperature);

        if (fluidBuilder.isGaseous) {
			builder.gaseous();
		}

        ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid
            .Properties(() -> fluidBuilder.stillFluid, () -> fluidBuilder.flowingFluid, builder)
            .bucket(() -> fluidBuilder.bucketItem)
            .block(() -> fluidBuilder.block);
        fluidBuilder.extraPlatformInfo = properties;
		return properties;
	}

    public static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
        return new LiquidBlock(() -> builder.stillFluid, properties);
    }

    public static BucketItem buildBucket(FluidBuilder builder) {
        return new BucketItemJS(builder);
    }

    public static class BucketItemJS extends BucketItem {
        public final FluidBuilder properties;

        public BucketItemJS(FluidBuilder b) {
            super(() -> b.stillFluid, new Properties().stacksTo(1).tab(KubeJS.tab));
            properties = b;
        }

        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return new FluidBucketWrapper(stack);
        }
    }
}
