package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public interface EntityKJS extends AsKJS<EntityJS> {
	@Override
	default EntityJS asKJS() {
        return KubeJS.PROXY.getWorld(((Entity) this).level).getEntity((Entity) this);
	}

	CompoundTag getPersistentDataKJS();
}
