package dev.latvian.kubejs.block.custom;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.block.events.DetectorBlockEventJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DetectorBlock extends Block {
	public static class Builder extends BlockBuilder {
		private String detectorId;

		public Builder(ResourceLocation i) {
			super(i);
			detectorId = (id.getNamespace().equals(KubeJS.MOD_ID) ? "" : (id.getNamespace() + ".")) + id.getPath().replace('/', '.');

			displayName(TextWrapper.string("KubeJS Detector [" + detectorId + "]").component());
		}

		public Builder detectorId(String id) {
			detectorId = id;
			displayName(TextWrapper.string("KubeJS Detector [" + detectorId + "]").component());
			return this;
		}

		@Override
		public Block createObject() {
			return new DetectorBlock(this);
		}

		@Override
		public void generateAssetJsons(AssetJsonGenerator generator) {
			generator.blockState(id, bs -> {
				bs.variant("powered=false", "kubejs:block/detector");
				bs.variant("powered=true", "kubejs:block/detector_on");
			});

			generator.itemModel(id, m -> m.parent(KubeJS.MOD_ID + ":block/detector"));
		}
	}

	private final Builder builder;

	public DetectorBlock(Builder b) {
		super(Properties.copy(Blocks.BEDROCK));
		builder = b;
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (level.isClientSide) {
            return;
        }
		val p = !blockState.getValue(BlockStateProperties.POWERED);

		if (p == level.hasNeighborSignal(blockPos)) {
			level.setBlock(blockPos, blockState.setValue(BlockStateProperties.POWERED, p), 2);
            new DetectorBlockEventJS(builder.detectorId, level, blockPos, p).post("block.detector." + builder.detectorId, p ? "powered" : "unpowered");
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.POWERED);
	}
}
