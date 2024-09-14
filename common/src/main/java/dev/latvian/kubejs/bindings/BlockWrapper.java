package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.util.Tags;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import lombok.val;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@JSInfo("Various block related helper functions")
public class BlockWrapper {
	public static Map<String, MaterialJS> getMaterial() {
		return MaterialListJS.INSTANCE.map;
	}

	public static BlockIDPredicate id(ResourceLocation id) {
		return new BlockIDPredicate(id);
	}

	public static BlockIDPredicate id(ResourceLocation id, Map<String, Object> properties) {
		val b = id(id);

		for (val entry : properties.entrySet()) {
			b.with(entry.getKey(), entry.getValue().toString());
		}

		return b;
	}

	public static BlockEntityPredicate entity(ResourceLocation id) {
		return new BlockEntityPredicate(id);
	}

	public static BlockPredicate custom(BlockPredicate predicate) {
		return predicate;
	}

	private static Map<String, Direction> facingMap;

    @JSInfo("Get a map of direction name to Direction. Functionally identical to Direction.ALL")
	public static Map<String, Direction> getFacing() {
		if (facingMap == null) {
			facingMap = new HashMap<>(6);

			for (Direction facing : Direction.values()) {
				facingMap.put(facing.getSerializedName(), facing);
			}
		}

		return facingMap;
	}

    @JSInfo("Gets a Block from a block id")
	public static Block getBlock(ResourceLocation id) {
		return RegistryInfos.BLOCK.getValue(id);
	}

    @JSInfo("Gets a blocks id from the Block")
    @Nullable
    public static ResourceLocation getId(Block block) {
        return RegistryInfos.BLOCK.getId(block);
    }

    @JSInfo("Gets a list of the id of all registered blocks")
	public static List<String> getTypeList() {
        val entries = RegistryInfos.BLOCK.entrySet();
		val list = new ArrayList<String>(entries.size());
        for (val entry : entries) {
            list.add(entry.getKey().location().toString());
        }
		return list;
	}

    @JSInfo("Gets a list of all block ids with provided tag")
	public static List<String> getTaggedIds(ResourceLocation tag) {
		Tag<Block> t = Tags.blocks().getTag(tag);
        if (t == null || t.getValues().isEmpty()) {
            return Collections.emptyList();
        }
        return t.getValues()
            .stream()
            .map(BlockWrapper::getId)
            .filter(Objects::nonNull)
            .map(ResourceLocation::toString)
            .collect(Collectors.toList());
    }
}