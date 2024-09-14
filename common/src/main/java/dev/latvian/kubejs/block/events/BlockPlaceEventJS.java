package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.entity.EntityEventJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author LatvianModder
 */
@AllArgsConstructor
public class BlockPlaceEventJS extends EntityEventJS {
	private final Entity entity;
	private final Level world;
	private final BlockPos pos;
	private final BlockState state;

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public WorldJS getWorld() {
		return worldOf(world);
	}

	@Override
	public EntityJS getEntity() {
		return entity == null ? null : entityOf(entity);
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(world, pos) {
			@Override
			public BlockState getBlockState() {
				return state;
			}
		};
	}
}