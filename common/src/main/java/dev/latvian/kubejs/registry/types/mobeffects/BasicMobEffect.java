package dev.latvian.kubejs.registry.types.mobeffects;

import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.script.ScriptType;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BasicMobEffect extends MobEffect {

	private final MobEffectBuilder.EffectTickCallback effectTickCallback;
	private final Map<ResourceLocation, AttributeModifier> modifierMap;
	private final Map<Attribute, AttributeModifier> attributeMap;
	private boolean modified = false;
	private final ResourceLocation id;

	public BasicMobEffect(Builder builder) {
		super(builder.category, builder.color);
		this.effectTickCallback = builder.effectTick;
		modifierMap = builder.attributeModifiers;
		attributeMap = new HashMap<>();
		this.id = builder.id;
	}

	@Override
	public void applyEffectTick(@NotNull LivingEntity livingEntity, int i) {
		try {
			effectTickCallback.applyEffectTick(livingEntity, i);
		} catch (Throwable e) {
			ScriptType.STARTUP.console.error("Error while ticking mob effect " + id + " for entity " + livingEntity.getName().getString(), e);
		}
	}

	private void applyAttributeModifications() {
		if (!modified) {
			modifierMap.forEach((r, m) -> attributeMap.put(RegistryInfos.ATTRIBUTE.getValue(r), m));
			modified = true;
		}
	}

	@Override
	public Map<Attribute, AttributeModifier> getAttributeModifiers() {
		this.applyAttributeModifications();
		return attributeMap;
	}

	@Override
	public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
		this.applyAttributeModifications();
		for (val entry : this.attributeMap.entrySet()) {
			val instance = attributeMap.getInstance(entry.getKey());
			if (instance != null) {
				instance.removeModifier(entry.getValue());
			}
		}
	}

	@Override
	public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
		this.applyAttributeModifications();
		for (val entry : this.attributeMap.entrySet()) {
			val instance = attributeMap.getInstance(entry.getKey());
			if (instance != null) {
				val modifier = entry.getValue();
				instance.removeModifier(modifier);
				instance.addPermanentModifier(new AttributeModifier(modifier.getId(), this.getDescriptionId() + " " + i, this.getAttributeModifierValue(i, modifier), modifier.getOperation()));
			}
		}

	}

	@Override
	public boolean isDurationEffectTick(int i, int j) {
		return this.effectTickCallback != null;
	}

	public static class Builder extends MobEffectBuilder {
		public Builder(ResourceLocation i) {
			super(i);
		}

		@Override
		public MobEffect createObject() {
			return new BasicMobEffect(this);
		}
	}
}
