package dev.latvian.kubejs.item;

import dev.latvian.kubejs.registry.RegistryEventJS;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.util.BuilderBase;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class ItemRegistryEventJS extends RegistryEventJS<Item> {
	public ItemRegistryEventJS() {
		super(RegistryInfo.ITEM);
	}

	public void create(String name, Consumer<BuilderBase<? extends Item>> callback) {
		callback.accept(this.create(name));
	}

	@Deprecated
	public Supplier<FoodBuilder> createFood(Supplier<FoodBuilder> builder) {
		return builder;
	}
}