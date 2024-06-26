package dev.latvian.kubejs.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.core.JsonSerializableKJS;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConstantIntValue.class)
public abstract class ConstantIntValueMixin implements JsonSerializableKJS {
	@Shadow
	@Final
	private int value;

	@Override
	public JsonElement toJsonKJS() {
		return new JsonPrimitive(value);
	}
}
