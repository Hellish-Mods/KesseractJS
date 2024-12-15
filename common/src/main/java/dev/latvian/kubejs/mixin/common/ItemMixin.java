package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.core.ItemKJS;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import me.shedaniel.architectury.registry.fuel.FuelRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author LatvianModder
 */
@Mixin(value = Item.class, priority = 1001)
public abstract class ItemMixin implements ItemKJS {
	@Unique
	private ItemBuilder kjs$itemBuilder;

	@Override
	@Nullable
	public ItemBuilder getItemBuilderKJS() {
		return kjs$itemBuilder;
	}

	@Override
	public void setItemBuilderKJS(ItemBuilder b) {
		kjs$itemBuilder = b;
	}

	@Override
	@Accessor("maxStackSize")
	public abstract void setMaxStackSizeKJS(int i);

	@Override
	@Accessor("maxDamage")
	public abstract void setMaxDamageKJS(int i);

	@Override
	@Accessor("craftingRemainingItem")
	public abstract void setCraftingRemainderKJS(Item i);

	@Override
	@Accessor("isFireResistant")
	public abstract void setFireResistantKJS(boolean b);

	@Override
	@Accessor("rarity")
	public abstract void setRarityKJS(Rarity r);

	@Override
	@RemapForJS("setBurnTime")
	public void setBurnTimeKJS(int i) {
		FuelRegistry.register(i, kjs$self());
	}

	@RemapForJS("getId")
	public String getIdKJS() {
		return KubeJSRegistries.items().getId(kjs$self()).toString();
	}

	@Override
	@Accessor("foodProperties")
	public abstract void setFoodPropertiesKJS(FoodProperties properties);

	@Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
	private void isFoilKJS(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.glow) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void getUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.useDuration != null) {
			ci.setReturnValue(kjs$itemBuilder.useDuration.applyAsInt(new ItemStackJS(itemStack)));
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void getUseAnimation(ItemStack itemStack, CallbackInfoReturnable<UseAnim> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.anim != null) {
			ci.setReturnValue(kjs$itemBuilder.anim);
		}
	}

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true)
	private void getName(ItemStack itemStack, CallbackInfoReturnable<Component> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.nameGetter != null) {
			ci.setReturnValue(kjs$itemBuilder.nameGetter.apply(itemStack));
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.use != null) {
			ItemStack itemStack = player.getItemInHand(hand);
			if (kjs$itemBuilder.use.use(level, player, hand)) {
				player.startUsingItem(hand);
				ci.setReturnValue(InteractionResultHolder.consume(player.getItemInHand(hand)));
			} else {
				ci.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		}
	}

	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
	private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.finishUsing != null) {
			ci.setReturnValue(kjs$itemBuilder.finishUsing.finishUsingItem(itemStack, level, livingEntity));
		}
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"))
	private void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.releaseUsing != null) {
			kjs$itemBuilder.releaseUsing.releaseUsing(itemStack, level, livingEntity, i);
		}
	}
}
