package dev.latvian.kubejs.stages;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class StageCreationEvent {
	@Getter
    private final Player player;
	private Stages stages;

	StageCreationEvent(Player p) {
		player = p;
	}

    public void setPlayerStages(Stages s) {
		stages = s;
	}

	@Nullable
	public Stages getPlayerStages() {
		return stages;
	}
}
