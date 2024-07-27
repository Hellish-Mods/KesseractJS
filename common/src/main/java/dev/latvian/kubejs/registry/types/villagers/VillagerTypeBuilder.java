package dev.latvian.kubejs.registry.types.villagers;

import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerTypeBuilder extends BuilderBase<VillagerType> {
	public VillagerTypeBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryInfo<VillagerType> getRegistryType() {
		return RegistryInfos.VILLAGER_TYPE;
	}

	@Override
	public VillagerType createObject() {
		return new VillagerType(id.getPath());
	}
}
