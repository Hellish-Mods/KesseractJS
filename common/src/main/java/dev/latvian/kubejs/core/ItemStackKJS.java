package dev.latvian.kubejs.core;

import dev.latvian.kubejs.item.ItemStackJS;

public interface ItemStackKJS extends AsKJS<ItemStackJS> {
	@Override
	default ItemStackJS asKJS() {
		return ItemStackJS.of(this);
	}

	void removeTagKJS();
}
