package dev.latvian.kubejs.client;

import dev.latvian.kubejs.registry.RegistryEventJS;
import dev.latvian.kubejs.registry.RegistryInfos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * @author LatvianModder
 */
public class SoundRegistryEventJS extends RegistryEventJS<SoundEvent> {

    public SoundRegistryEventJS() {
        super(RegistryInfos.SOUND_EVENT);
    }

	public void register(ResourceLocation r) {
		create(r.toString());
	}
}