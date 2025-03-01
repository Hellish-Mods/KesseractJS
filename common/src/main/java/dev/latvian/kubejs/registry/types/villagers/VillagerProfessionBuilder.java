package dev.latvian.kubejs.registry.types.villagers;

import com.google.common.collect.ImmutableSet;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.mods.rhino.annotations.typing.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@ReturnsSelf
public class VillagerProfessionBuilder extends BuilderBase<VillagerProfession> {
	public transient PoiType poiType;
	public transient ImmutableSet<Item> requestedItems;
	public transient ImmutableSet<Block> secondaryPoi;
	public transient SoundEvent workSound;

	public VillagerProfessionBuilder(ResourceLocation i) {
		super(i);
		poiType = PoiType.HOME;
		requestedItems = ImmutableSet.of();
		secondaryPoi = ImmutableSet.of();
		workSound = null;
	}

	@Override
	public final RegistryInfo<VillagerProfession> getRegistryType() {
		return RegistryInfos.VILLAGER_PROFESSION;
	}

	@Override
	public VillagerProfession createObject() {
		return new VillagerProfession(id.getPath(), poiType, requestedItems, secondaryPoi, workSound);
	}

	public VillagerProfessionBuilder poiType(PoiType t) {
		poiType = t;
		return this;
	}

	public VillagerProfessionBuilder requestedItems(Item[] t) {
		requestedItems = ImmutableSet.copyOf(t);
		return this;
	}

	public VillagerProfessionBuilder secondaryPoi(Block[] t) {
		secondaryPoi = ImmutableSet.copyOf(t);
		return this;
	}

	public VillagerProfessionBuilder workSound(SoundEvent t) {
		workSound = t;
		return this;
	}
}