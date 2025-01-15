package dev.latvian.kubejs.block;

import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.mod.util.color.SimpleColorWithAlpha;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface BlockTintFunction {
	Color getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index);

	record Fixed(Color color) implements BlockTintFunction {
		@Override
		public Color getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
			return color;
		}
	}

	class Mapped implements BlockTintFunction {
		public final Int2ObjectMap<BlockTintFunction> map = new Int2ObjectArrayMap<>(1);

		@Override
		public Color getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
			var f = map.get(index);
			return f == null ? null : f.getColor(state, level, pos, index);
		}
	}

    //colors
	Color DEFAULT_FOLIAGE_COLOR = new SimpleColor(FoliageColor.getDefaultColor());
	Color[] REDSTONE_COLORS = new Color[16];
    //tint
	BlockTintFunction GRASS = (s, l, p, i) -> new SimpleColor(l == null || p == null ? GrassColor.get(0.5, 1.0) : BiomeColors.getAverageGrassColor(l, p));
	BlockTintFunction FOLIAGE = (s, l, p, i) -> l == null || p == null ? DEFAULT_FOLIAGE_COLOR : new SimpleColor(BiomeColors.getAverageFoliageColor(l, p));
	Fixed EVERGREEN_FOLIAGE = new Fixed(new SimpleColor(FoliageColor.getEvergreenColor()));
	Fixed BIRCH_FOLIAGE = new Fixed(new SimpleColor(FoliageColor.getBirchColor()));
	BlockTintFunction WATER = (s, l, p, i) -> l == null || p == null ? null : new SimpleColorWithAlpha(BiomeColors.getAverageWaterColor(l, p));
	BlockTintFunction REDSTONE = (state, level, pos, index) -> {
		if (REDSTONE_COLORS[0] == null) {
			for (int i = 0; i < REDSTONE_COLORS.length; i++) {
				REDSTONE_COLORS[i] = new SimpleColor(RedStoneWireBlock.getColorForPower(i));
			}
		}

		return REDSTONE_COLORS[state.getValue(BlockStateProperties.POWER)];
	};

	@Nullable
	static BlockTintFunction of(Object o) {
		if (o == null || Undefined.isUndefined(o)) {
			return null;
		} else if (o instanceof BlockTintFunction f) {
			return f;
		} else if (o instanceof List<?> list) {
			var map = new Mapped();

			for (int i = 0, size = list.size(); i < size; i++) {
				var fn = of(list.get(i));
				if (fn != null) {
					map.map.put(i, fn);
				}
			}

			return map;
		} else if (o instanceof CharSequence) {
            var fn = switch (o.toString()) {
                case "grass" -> GRASS;
                case "foliage" -> FOLIAGE;
                case "evergreen_foliage" -> EVERGREEN_FOLIAGE;
                case "birch_foliage" -> BIRCH_FOLIAGE;
                case "water" -> WATER;
                case "redstone" -> REDSTONE;
                default -> null;
            };
            if (fn != null) {
                return fn;
            }
//		} else if (o instanceof BaseFunction function) {
//			return (BlockTintFunction) NativeJavaObject.createInterfaceAdapter(BlockTintFunction.class, function);
		}

		return new Fixed(ColorWrapper.of(o));
	}
}
