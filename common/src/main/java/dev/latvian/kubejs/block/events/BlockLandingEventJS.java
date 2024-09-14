package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.entity.EntityEventJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class BlockLandingEventJS extends EntityEventJS {
    private final Level level;
    @Getter
    private final BlockPos pos;
    @Getter
    private final BlockState fallState;
    @Getter
    private final BlockState landOn;
    private final FallingBlockEntity entity;

    public boolean isServerSide() {
        return level instanceof ServerLevel;
    }

    public boolean isClientSide() {
        return level instanceof ClientLevel;
    }

    public WorldJS getWorld() {
        if (isServerSide()) {
            return new ServerWorldJS(ServerJS.instance, (ServerLevel) level);
        }
        if (isClientSide()) {
            return ClientWorldJS.getInstance();
        }
        return null;
    }

    public FallingBlockEntity getEntityRaw() {
        return entity;
    }

    public EntityJS getEntity() {
        return entityOf(entity);
    }
}
