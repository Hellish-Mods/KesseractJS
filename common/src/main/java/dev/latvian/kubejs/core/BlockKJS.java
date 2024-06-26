package dev.latvian.kubejs.core;

import dev.latvian.kubejs.block.BlockBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public interface BlockKJS {
	@Nullable
	BlockBuilder getBlockBuilderKJS();

	void setBlockBuilderKJS(BlockBuilder b);

	void setMaterialKJS(Material v);

	void setHasCollisionKJS(boolean v);

	void setExplosionResistanceKJS(float v);

	void setIsRandomlyTickingKJS(boolean v);

	void setSoundTypeKJS(SoundType v);

	void setFrictionKJS(float v);

	void setSpeedFactorKJS(float v);

	void setJumpFactorKJS(float v);

	default List<BlockState> getBlockStatesKJS() {
		if (!(this instanceof Block)) {
			return Collections.emptyList();
		}

		return ((Block) this).getStateDefinition().getPossibleStates();
	}
}
