package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.event.EventJS;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class HideJEIEventJS<T> extends EventJS {
	private final IJeiRuntime runtime;
	private final IIngredientType<T> type;
	private final Function<Object, Predicate<T>> function;
	private final HashSet<T> hidden;
	private final Predicate<T> isValid;
	private final Collection<T> allIngredients;

    public HideJEIEventJS(
        IJeiRuntime runtime,
        IIngredientType<T> type,
        Function<Object, Predicate<T>> toIngredient,
        Predicate<T> filter
    ) {
		this.runtime = runtime;
		this.type = type;
		function = toIngredient;
		hidden = new HashSet<>();
		isValid = filter;
		allIngredients = this.runtime.getIngredientManager().getAllIngredients(this.type);
	}

	public Collection<T> getAllIngredients() {
		return allIngredients;
	}

	public void hide(Object o) {
		Predicate<T> p = function.apply(o);

		for (T value : allIngredients) {
			if (p.test(value)) {
				hidden.add(value);
			}
		}
	}

	public void hideAll() {
		hidden.addAll(allIngredients);
	}

	@Override
	protected void afterPosted(boolean result) {
		if (!hidden.isEmpty()) {
			runtime.getIngredientManager().removeIngredientsAtRuntime(type, hidden.stream().filter(isValid).collect(Collectors.toList()));
		}
	}
}