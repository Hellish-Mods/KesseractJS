package dev.latvian.kubejs.item.events;

import dev.latvian.kubejs.item.FoodBuilder;
import dev.latvian.kubejs.registry.RegistryEventJS;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfos;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class ItemRegistryEventJS extends RegistryEventJS<Item> {
	public ItemRegistryEventJS() {
		super(RegistryInfos.ITEM);
	}

	public void create(String name, Consumer<BuilderBase<? extends Item>> callback) {
		callback.accept(this.create(name));
	}

	@Deprecated
	public Supplier<FoodBuilder> createFood(Supplier<FoodBuilder> builder) {
		return builder;
	}
}