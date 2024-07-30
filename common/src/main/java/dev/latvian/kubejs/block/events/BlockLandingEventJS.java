package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.entity.EntityEventJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author ZZZank
 */
public class BlockLandingEventJS extends EntityEventJS {
    private final Level level;
    private final BlockPos pos;
    private final BlockState fallState;
    private final BlockState landOn;
    private final FallingBlockEntity entity;

    public BlockLandingEventJS(
        Level level,
        BlockPos pos,
        BlockState fallState,
        BlockState landOn,
        FallingBlockEntity entity
    ) {
        this.level = level;
        this.pos = pos;
        this.fallState = fallState;
        this.landOn = landOn;
        this.entity = entity;
    }

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

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getFallState() {
        return this.fallState;
    }

    public BlockState getLandOn() {
        return this.landOn;
    }

    public EntityJS getEntity() {
        return new EntityJS(getLevel(), entity);
    }
}
