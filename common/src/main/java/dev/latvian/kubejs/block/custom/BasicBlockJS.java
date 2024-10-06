package dev.latvian.kubejs.block.custom;

import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.block.RandomTickCallbackJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class BasicBlockJS extends Block {
    private static BlockBuilder builderRef;

	public final BlockBuilder properties;
	public final VoxelShape shape;

	public BasicBlockJS(@NotNull BlockBuilder builder) {
		super(createPropAndCacheBuilderReferenceBecauseBlockConstructorUsedBlockBuilderEvenBeforeItsInitializedAllBecauseItsPoorlyDesigned(builder));
		properties = builder;
		shape = builder.createShape();

		if (properties.waterlogged) {
			registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
		}
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        if (builderOrFallback().waterlogged) {
			builder.add(BlockStateProperties.WATERLOGGED);
		}
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return properties.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (!properties.waterlogged) {
			return defaultBlockState();
		}

		return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
		if (properties.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return state;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return properties.transparent || !(properties.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED));
	}

	@Override
	@Deprecated
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (properties.randomTickCallback != null) {
			val containerJS = new BlockContainerJS(level, pos);
			try {
				properties.randomTickCallback.accept(new RandomTickCallbackJS(containerJS, random));
			} catch (Exception e) {
				LOGGER.error("Error while random ticking custom block {}: {}", this, e);
			}
		}
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return properties.randomTickCallback != null;
	}

	@Override
	@Deprecated
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return properties.transparent ? Shapes.empty() : super.getVisualShape(state, level, pos, ctx);
	}

	@Override
	@Deprecated
	@Environment(EnvType.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return properties.transparent ? 1F : super.getShadeBrightness(state, level, pos);
	}

	@Override
	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean skipRendering(BlockState state, BlockState state2, Direction direction) {
		return properties.transparent ? (state2.is(this) || super.skipRendering(state, state2, direction)) : super.skipRendering(state, state2, direction);
	}

    /**
     * @see BasicBlockJS#builderOrFallback()
     */
    private static Properties createPropAndCacheBuilderReferenceBecauseBlockConstructorUsedBlockBuilderEvenBeforeItsInitializedAllBecauseItsPoorlyDesigned(@NotNull BlockBuilder builder) {
        return (builderRef = Objects.requireNonNull(builder)).createProperties();
    }

    /**
     * dirty hack to make sure there's a valid {@link BlockBuilder} for
     * {@link Block#createBlockStateDefinition(StateDefinition.Builder)} when {@link Block} is on initialization
     */
    private BlockBuilder builderOrFallback() {
        if (properties != null) {
            return properties;
        }
        val tmp = builderRef;
        builderRef = null;
        return tmp;
    }
}