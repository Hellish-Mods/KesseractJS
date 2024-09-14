package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientWithCustomPredicateJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAnyIngredientJS;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.kubejs.recipe.ingredientaction.CustomIngredientActionCallback;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

@JSInfo("Various Ingredient related helper methods")
public class IngredientWrapper {
    @JSInfo("A completely empty ingredient that will only match air")
	public static IngredientJS getNone() {
		return ItemStackJS.EMPTY;
	}

    @JSInfo("An ingredient that matches everything")
	public static IngredientJS getAll() {
		return MatchAllIngredientJS.INSTANCE;
	}

    @JSInfo("Returns an ingredient of the input")
	public static IngredientJS of(Object object) {
		return IngredientJS.of(object);
	}

    @JSInfo("Returns an ingredient of the input, with the specified count")
	public static IngredientJS of(Object object, int count) {
		return of(object).withCount(Math.max(1, count));
	}

    @JSInfo("Make a custom ingredient where a match must match the provided predicate function")
	public static IngredientJS custom(Predicate<ItemStackJS> predicate) {
		return predicate::test;
	}

    @JSInfo("Make a custom ingredient where items must match both the parent ingredient and the custom predicate function")
	public static IngredientJS custom(IngredientJS parent, Predicate<ItemStackJS> predicate) {
		if (RecipeEventJS.customIngredientMap != null) {
			IngredientWithCustomPredicateJS ingredient = new IngredientWithCustomPredicateJS(UUID.randomUUID(), parent, i -> predicate.test(new ItemStackJS(i)));
			RecipeEventJS.customIngredientMap.put(ingredient.uuid, ingredient);
			return ingredient;
		}

		return new IngredientWithCustomPredicateJS(null, parent, i -> predicate.test(new ItemStackJS(i)));
	}

    @JSInfo("Make a custom ingredient where an item must match both the parent ingredient and the item's nbt must match the custom predicate function")
	public static IngredientJS customNBT(IngredientJS parent, Predicate<CompoundTag> predicate) {
		return custom(parent, is -> is.hasNBT() && predicate.test(is.getNbt()));
	}

	public static IngredientJS matchAny(Object objects) {
		MatchAnyIngredientJS ingredient = new MatchAnyIngredientJS();
		ingredient.addAll(objects);
		return ingredient;
	}

    @JSInfo("Register a custom ingredient action for use in recipes with Recipe#customIngredientAction")
	public static void registerCustomIngredientAction(String id, CustomIngredientActionCallback callback) {
		CustomIngredientAction.MAP.put(id, callback);
	}

	public static boolean isIngredient(@Nullable Object o) {
		return o instanceof IngredientJS;
	}
}