package dev.latvian.kubejs.block.predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author ZZZank
 */
public interface VanillaPredicates {
    BlockBehaviour.StatePredicate STATE_TRUE = (blockState, blockGetter, blockPos) -> true;
    BlockBehaviour.StatePredicate STATE_FALSE = (blockState, blockGetter, blockPos) -> false;
    BlockBehaviour.StateArgumentPredicate<?> STATE_ARGUMENT_TRUE = (blockState, blockGetter, blockPos, o) -> true;
    BlockBehaviour.StateArgumentPredicate<?> STATE_ARGUMENT_FALSE = (blockState, blockGetter, blockPos, o) -> false;

    interface Spawn extends BlockBehaviour.StateArgumentPredicate<EntityType<?>> {
        Spawn TRUE = (blockState, blockGetter, blockPos, o) -> true;
        Spawn FALSE = (blockState, blockGetter, blockPos, o) -> false;

        @Override
        boolean test(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> object);
    }
}
