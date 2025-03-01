package dev.latvian.kubejs.registry.types.potion;

import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.mods.rhino.annotations.typing.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ReturnsSelf
public class PotionBuilder extends BuilderBase<Potion> {
	public transient List<MobEffectInstance> mobEffects;

	public PotionBuilder(ResourceLocation i) {
		super(i);
		mobEffects = new ArrayList<>();
	}

	@Override
	public RegistryInfo<Potion> getRegistryType() {
		return RegistryInfos.POTION;
	}

	@Override
	public Potion createObject() {
		return new Potion(id.getPath(), mobEffects.toArray(new MobEffectInstance[0]));
	}

	public PotionBuilder addEffect(MobEffectInstance effect) {
		mobEffects.add(effect);
		return this;
	}

	public PotionBuilder effect(MobEffect effect) {
		return effect(effect, 0, 0);
	}

	public PotionBuilder effect(MobEffect effect, int duration) {
		return effect(effect, duration, 0);
	}

	public PotionBuilder effect(MobEffect effect, int duration, int amplifier) {
		return effect(effect, duration, amplifier, false, true);
	}

	public PotionBuilder effect(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible) {
		return effect(effect, duration, amplifier, ambient, visible, visible);
	}

	public PotionBuilder effect(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
		return effect(effect, duration, amplifier, ambient, visible, showIcon, null);
	}

	public PotionBuilder effect(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon, @Nullable MobEffectInstance hiddenEffect) {
		return addEffect(new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon, hiddenEffect));
	}
}
