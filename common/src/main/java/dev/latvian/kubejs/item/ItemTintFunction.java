package dev.latvian.kubejs.item;

import dev.latvian.kubejs.core.BlockKJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.val;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface ItemTintFunction {
	Color getColor(ItemStack stack, int index);

	default ItemColor asItemColor() {
		return (stack, index) -> getColor(stack, index).getArgbKJS();
	}

	record Fixed(Color color) implements ItemTintFunction {
		@Override
		public Color getColor(ItemStack stack, int index) {
			return color;
		}
	}

	class Mapped implements ItemTintFunction {
		public final Int2ObjectMap<ItemTintFunction> map = new Int2ObjectArrayMap<>(1);

		@Override
		public Color getColor(ItemStack stack, int index) {
			val f = map.get(index);
			return f == null ? null : f.getColor(stack, index);
		}
	}

	ItemTintFunction BLOCK = (stack, index) -> {
		if (stack.getItem() instanceof BlockItem block) {
			val s = block.getBlock().defaultBlockState();
			val internal = ((BlockKJS) s.getBlock()).getBlockBuilderKJS();

			if (internal != null && internal.tint != null) {
				return internal.tint.getColor(s, null, null, index);
			}
		}

		return null;
	};

	ItemTintFunction POTION = (stack, index) -> new SimpleColor(PotionUtils.getColor(stack));
	ItemTintFunction MAP = (stack, index) -> new SimpleColor(MapItem.getColor(stack));
	ItemTintFunction DISPLAY_COLOR_NBT = (stack, index) -> {
		val tag = stack.getTagElement("display");

		if (tag != null && tag.contains("color", 99)) {
			return new SimpleColor(tag.getInt("color"));
		}

		return null;
	};

	@Nullable
	static ItemTintFunction of(Object o) {
		if (o == null || Undefined.isUndefined(o)) {
			return null;
		} else if (o instanceof ItemTintFunction f) {
			return f;
		} else if (o instanceof List<?> list) {
			val map = new Mapped();

			for (int i = 0; i < list.size(); i++) {
				val f = of(list.get(i));

				if (f != null) {
					map.map.put(i, f);
				}
			}

			return map;
		} else if (o instanceof CharSequence) {
			val fn = switch (o.toString()) {
				case "block" -> BLOCK;
				case "potion" -> POTION;
				case "map" -> MAP;
				case "display_color_nbt" -> DISPLAY_COLOR_NBT;
				default -> null;
			};
			if (fn != null) {
				return fn;
			}
		} else if (o instanceof BaseFunction function) {
			return (ItemTintFunction) NativeJavaObject.createInterfaceAdapter(ItemTintFunction.class, function);
		}

		return new Fixed(ColorWrapper.of(o));
	}
}