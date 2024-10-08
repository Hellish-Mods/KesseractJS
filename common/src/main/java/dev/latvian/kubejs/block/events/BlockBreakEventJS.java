package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import lombok.AllArgsConstructor;
import me.shedaniel.architectury.utils.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@AllArgsConstructor
public class BlockBreakEventJS extends PlayerEventJS {
	private final ServerPlayer entity;
	private final Level world;
	private final BlockPos pos;
	private final BlockState state;
	@Nullable
	private final IntValue xp;

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(world, pos) {
			@Override
			public BlockState getBlockState() {
				return state;
			}
		};
	}

	public int getXp() {
		if (xp == null) {
			return 0;
		}
		return xp.getAsInt();
	}

	public void setXp(int xp) {
		if (this.xp != null) {
			this.xp.accept(xp);
		}
	}
}