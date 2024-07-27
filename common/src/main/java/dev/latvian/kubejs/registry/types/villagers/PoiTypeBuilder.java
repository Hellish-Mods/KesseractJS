package dev.latvian.kubejs.registry.types.villagers;

import com.google.common.collect.ImmutableSet;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

/**
 * @author ZZZank
 */
public class PoiTypeBuilder extends BuilderBase<PoiType> {
	public transient Set<BlockState> blockStates;
	public transient int maxTickets;
	public transient int validRange;

	public PoiTypeBuilder(ResourceLocation i) {
		super(i);
		blockStates = ImmutableSet.of();
		maxTickets = 1;
		validRange = 1;
	}

	@Override
	public final RegistryInfo<PoiType> getRegistryType() {
		return RegistryInfos.POINT_OF_INTEREST_TYPE;
	}

	@Override
	public PoiType createObject() {
		return new PoiType(id.getPath(), blockStates, maxTickets, validRange);
	}

	public PoiTypeBuilder blocks(BlockState[] r) {
		blockStates = ImmutableSet.copyOf(r);
		return this;
	}

	public PoiTypeBuilder block(Block r) {
		blockStates = ImmutableSet.copyOf(r.getStateDefinition().getPossibleStates());
		return this;
	}

	public PoiTypeBuilder maxTickets(int i) {
		maxTickets = i;
		return this;
	}

	public PoiTypeBuilder validRange(int i) {
		validRange = i;
		return this;
	}
}