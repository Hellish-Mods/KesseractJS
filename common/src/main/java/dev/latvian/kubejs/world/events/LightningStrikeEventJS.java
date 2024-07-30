package dev.latvian.kubejs.world.events;

import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * @author ZZZank
 */
public class LightningStrikeEventJS extends ServerEventJS {
    public final LightningBolt bolt;
    public final Level level;
    public final Vec3 pos;
    public final List<Entity> toStrike;

    public LightningStrikeEventJS(LightningBolt bolt, Level level, Vec3 pos, List<Entity> toStrike) {
        this.bolt = bolt;
        this.level = level;
        this.pos = pos;
        this.toStrike = toStrike;
    }
}
