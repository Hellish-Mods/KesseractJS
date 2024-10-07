package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.entity.EntityEventJS;
import dev.latvian.kubejs.entity.EntityJS;
import lombok.AllArgsConstructor;
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
    public final BlockPos pos;
    public final BlockState fallState;
    public final BlockState landOn;
    public final FallingBlockEntity entity;

    public boolean isServerSide() {
        return level instanceof ServerLevel;
    }

    public boolean isClientSide() {
        return level instanceof ClientLevel;
    }

    public Level getLevelVanilla() {
        return level;
    }

    public FallingBlockEntity getEntityVanilla() {
        return entity;
    }

    public EntityJS getEntity() {
        return entityOf(entity);
    }
}
