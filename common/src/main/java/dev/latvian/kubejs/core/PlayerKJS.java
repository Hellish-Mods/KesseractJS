package dev.latvian.kubejs.core;

import dev.latvian.kubejs.stages.Stages;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public interface PlayerKJS {
	@Nullable
	Stages kjs$getStagesRaw();

	void kjs$setStages(Stages p);

	Stages kjs$getStages();
}
