package dev.latvian.kubejs.registry.types.particle;

import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

public class ParticleTypeBuilder extends BuilderBase<ParticleType> {
	public transient boolean overrideLimiter;
	public transient ParticleOptions.Deserializer deserializer;

	public ParticleTypeBuilder(ResourceLocation i) {
		super(i);
		overrideLimiter = false;
	}

	@Override
	public final RegistryInfo<ParticleType> getRegistryType() {
		return RegistryInfos.PARTICLE_TYPE;
	}

	@Override
	public ParticleType<?> createObject() {
		if (deserializer != null) {
			return new ComplexParticleType(overrideLimiter, deserializer);
		}

		return new BasicParticleType(overrideLimiter);
	}

	public ParticleTypeBuilder overrideLimiter(boolean o) {
		overrideLimiter = o;
		return this;
	}

	// TODO: Figure out if this is even needed
	public ParticleTypeBuilder deserializer(ParticleOptions.Deserializer d) {
		deserializer = d;
		return this;
	}
}
