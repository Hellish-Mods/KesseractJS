package dev.latvian.kubejs.integration.jei;

import com.google.common.base.Predicates;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.ListJS;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.HashMap;

/**
 * @author LatvianModder
 */
public class HideCustomJEIEventJS extends EventJS {
	private final IJeiRuntime runtime;
	private final HashMap<IIngredientType<?>, HideJEIEventJS<?>> events;

	public HideCustomJEIEventJS(IJeiRuntime r) {
		runtime = r;
		events = new HashMap<>();
	}

    @SuppressWarnings("unchecked")
	public <T> HideJEIEventJS<T> get(IIngredientType<T> ingredientType) {
        return (HideJEIEventJS<T>) events.computeIfAbsent(
            ingredientType,
            type -> new HideJEIEventJS<>(
                runtime,
                type,
                o -> ListJS.orSelf(o)::contains,
                Predicates.alwaysTrue()
            )
        );
	}

	@Override
	protected void afterPosted(boolean result) {
		for (HideJEIEventJS<?> eventJS : events.values()) {
			eventJS.afterPosted(result);
		}
	}
}