package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.events.WorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DetectorBlockEventJS extends WorldEventJS {
	@Getter
    private final String detectorId;
	private final Level level;
	@Getter
    private final BlockPos pos;
	@Getter
    private final boolean powered;
	@Getter
    private final BlockContainerJS block;

	public DetectorBlockEventJS(String i, Level l, BlockPos p, boolean pow) {
		detectorId = i;
		level = l;
		pos = p;
		powered = pow;
		block = new BlockContainerJS(level, pos);
	}

    @Override
	public WorldJS getWorld() {
		return worldOf(level);
	}
}
