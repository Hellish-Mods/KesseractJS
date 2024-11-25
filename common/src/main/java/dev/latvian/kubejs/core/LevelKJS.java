package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.level.Level;

public interface LevelKJS extends AsKJS<WorldJS> {
	@Override
	default WorldJS asKJS() {
		return KubeJS.PROXY.getWorld((Level) this);
	}
}
