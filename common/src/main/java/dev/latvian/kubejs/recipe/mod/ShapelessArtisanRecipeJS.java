package dev.latvian.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;

public class ShapelessArtisanRecipeJS extends ShapelessRecipeJS {
	private JsonArray getOrCreateArray(String key) {
		JsonArray a = (JsonArray) json.get(key);

		if (a == null) {
			a = new JsonArray();
			json.add(key, a);
		}

		return a;
	}

	public ShapelessArtisanRecipeJS tool(IngredientJS ingredient, int damage) {
		JsonElement e = ingredient.toJson();

		if (e instanceof JsonObject) {
			((JsonObject) e).addProperty("damage", damage);
			getOrCreateArray("tools").add(e);
		} else {
			JsonObject o = new JsonObject();
			o.addProperty("item", ingredient.getFirst().getId());
			o.addProperty("damage", damage);
			getOrCreateArray("tools").add(o);
		}

		return this;
	}

	public ShapelessArtisanRecipeJS fluid(FluidStackJS fluid) {
		JsonObject o = new JsonObject();
		o.addProperty("fluid", fluid.getId());
		o.addProperty("amount", fluid.getAmount());
		json.add("fluidIngredient", o);
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS consumeSecondaryIngredients(boolean b) {
		json.addProperty("consumeSecondaryIngredients", b);
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS secondaryIngredient(IngredientJS ingredient) {
		getOrCreateArray("secondaryIngredients").add(ingredient.toJson());
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS extraOutput(ItemStackJS item) {
		getOrCreateArray("extraOutput").add(item.toResultJson());
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS mirrored(boolean b) {
		json.addProperty("mirrored", b);
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS minimumTier(int b) {
		json.addProperty("minimumTier", b);
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS maximumTier(int b) {
		json.addProperty("maximumTier", b);
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS experienceRequired(int b) {
		json.addProperty("experienceRequired", b);
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS levelRequired(int b) {
		json.addProperty("levelRequired", b);
		save();
		return this;
	}

	public ShapelessArtisanRecipeJS consumeExperience(boolean b) {
		json.addProperty("consumeExperience", b);
		save();
		return this;
	}
}
