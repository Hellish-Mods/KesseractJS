package dev.latvian.kubejs.registry.types;

import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilder extends BuilderBase<SoundEvent> {
	public SoundEventBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryInfo<SoundEvent> getRegistryType() {
		return RegistryInfos.SOUND_EVENT;
	}

	@Override
	public SoundEvent createObject() {
		return new SoundEvent(id);
	}
}
