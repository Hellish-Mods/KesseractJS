package dev.latvian.kubejs.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class KubeJSBlockProperties extends BlockBehaviour.Properties {
	public final BlockBuilder blockBuilder;

	public KubeJSBlockProperties(BlockBuilder blockBuilder) {
		super(blockBuilder.material.getMinecraftMaterial(), blockBuilder.materialColorFn);
		this.blockBuilder = blockBuilder;
	}
}
