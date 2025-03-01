package dev.latvian.kubejs.block.custom.builder;

import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.mods.rhino.annotations.typing.ReturnsSelf;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

@ReturnsSelf
public class FallingBlockBuilder extends BlockBuilder {
    /**
     * @see net.minecraft.world.level.block.GravelBlock#getDustColor(BlockState, BlockGetter, BlockPos)
     */
	private int dustColor = -8356741;

	public FallingBlockBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public Block createObject() {
        return new FallingBlockJS();
	}

	public FallingBlockBuilder dustColor(Color color) {
		dustColor = color.getRgbKJS();
		return this;
	}

    private class FallingBlockJS extends FallingBlock {
        public FallingBlockJS() {
            super(FallingBlockBuilder.this.createProperties());
        }

        @Override
        public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
            return dustColor;
        }
    }
}
