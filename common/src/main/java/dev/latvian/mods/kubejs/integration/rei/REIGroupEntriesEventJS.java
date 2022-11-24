package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.Tags;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class REIGroupEntriesEventJS extends EventJS {
	public final CollapsibleEntryRegistry registry;

	public REIGroupEntriesEventJS(CollapsibleEntryRegistry registry) {
		this.registry = registry;
	}

	// shortcut impl for the two builtin entry types
	public void groupItems(ResourceLocation groupId, Component description, IngredientJS entries) {
		group(groupId, description, EntryIngredients.ofIngredient(entries.createVanillaIngredient()));
	}

	public void groupFluids(ResourceLocation groupId, Component description, FluidStackJS... entries) {
		group(groupId, description, EntryIngredients.of(VanillaEntryTypes.FLUID, CollectionUtils.map(entries, FluidStackJS::getFluidStack)));
	}

	public void groupEntries(ResourceLocation groupId, Component description, ResourceLocation entryTypeId, Object entries) {
		var entryType = KubeJSREIPlugin.getTypeOrThrow(entryTypeId);
		var wrapper = KubeJSREIPlugin.getWrapperOrFallback(entryType);
		var entryList = ListJS.orSelf(entries);

		var list = new ArrayList<EntryStack<?>>(entryList.size());
		for (var entry : entryList) {
			var stacks = wrapper.wrap(entry);
			if (stacks != null && !stacks.isEmpty()) {
				list.addAll(stacks);
			}
		}

		group(groupId, description, list);
	}

	public void groupSameItem(ResourceLocation group, Component description, ItemStackJS item) {
		groupItemsIf(group, description, item.ignoreNBT());
	}

	public void groupSameFluid(ResourceLocation group, Component description, FluidStackJS fluid) {
		groupFluidsIf(group, description, stack -> stack.getFluid().equals(fluid.getFluid()));
	}

	// tag grouping, only for builtin entry types
	public void groupItemsByTag(ResourceLocation groupId, Component description, ResourceLocation tags) {
		group(groupId, description, EntryIngredients.ofItemTag(Tags.item(tags)));
	}

	public void groupFluidsByTag(ResourceLocation groupId, Component description, ResourceLocation tags) {
		group(groupId, description, EntryIngredients.ofFluidTag(Tags.fluid(tags)));
	}

	// predicate-based grouping, again with shortcuts for builtin entry types
	public void groupItemsIf(ResourceLocation groupId, Component description, IngredientJS predicate) {
		// IngredientJS is already a predicate for ItemStackJS, so just use testVanilla
		registry.group(groupId, description, VanillaEntryTypes.ITEM, (item) -> predicate.testVanilla(item.getValue()));
	}

	public void groupFluidsIf(ResourceLocation groupId, Component description, Predicate<FluidStackJS> predicate) {
		registry.group(groupId, description, VanillaEntryTypes.FLUID, (fluid) -> predicate.test(FluidStackJS.of(fluid.getValue())));
	}

	// the difference between these next two methods:
	// the first method only groups entries of a single type, and uses a value-based predicate,
	// while the second method uses an EntryStack-based predicate
	// (which is more flexible and can be spanned across multiple entry types)
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void groupEntriesIf(ResourceLocation groupId, Component description, ResourceLocation entryTypeId, Predicate predicate) {
		var entryType = KubeJSREIPlugin.getTypeOrThrow(entryTypeId);
		var wrapper = KubeJSREIPlugin.getWrapperOrFallback(entryType);
		registry.group(groupId, description, entryType, (entry) -> predicate.test(entry.getValue()));
	}

	public void groupAnyIf(ResourceLocation groupId, Component description, Predicate<EntryStack<?>> predicate) {
		registry.group(groupId, description, predicate);
	}

	private void group(ResourceLocation groupId, Component description, List<EntryStack<?>> entries) {
		registry.group(groupId, description, entries);
	}

}