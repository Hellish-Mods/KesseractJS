package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.PlayerKJS;
import dev.latvian.kubejs.stages.Stages;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author LatvianModder
 */
@Mixin(value = Player.class, priority = 1001)
public abstract class PlayerMixin implements PlayerKJS {
	@Unique
    private Stages kjs$stages;

	@Override
	@Nullable
	public Stages kjs$getStagesRaw() {
		return kjs$stages;
	}

	@Override
	@HideFromJS
	public void kjs$setStages(Stages p) {
		kjs$stages = p;
	}

	@Override
	@RemapForJS("getStages")
	public Stages kjs$getStages() {
		if (kjs$stages != null) {
			return kjs$stages;
		}

		return Stages.get((Player) (Object) this);
	}
}
