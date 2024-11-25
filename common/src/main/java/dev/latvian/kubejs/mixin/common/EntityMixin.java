package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.EntityKJS;
import dev.latvian.mods.rhino.util.MapLike;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityKJS {
	@Unique
	private final CompoundTag kjs$persistentData = new CompoundTag();

	@Override
	public CompoundTag getPersistentDataKJS() {
		return kjs$persistentData;
	}

	@Inject(method = "saveWithoutId", at = @At("RETURN"))
	private void saveKJS(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ci) {
		tag.put("KubeJSPersistentData", kjs$persistentData);
	}

	@Inject(method = "load", at = @At("RETURN"))
	private void loadKJS(CompoundTag tag, CallbackInfo ci) {
		((MapLike<?, ?>) kjs$persistentData).clearML();
		kjs$persistentData.merge(tag.getCompound("KubeJSPersistentData"));
	}
}
