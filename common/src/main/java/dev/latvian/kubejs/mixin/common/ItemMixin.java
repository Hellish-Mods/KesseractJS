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
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
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

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(value = Item.class, priority = 1001)
public abstract class ItemMixin implements ItemKJS {
	@Unique
	private ItemBuilder itemBuilderKJS;

	@Override
	@Nullable
	public ItemBuilder getItemBuilderKJS() {
		return itemBuilderKJS;
	}

	@Override
	public void setItemBuilderKJS(ItemBuilder b) {
		itemBuilderKJS = b;
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
		if (itemBuilderKJS != null && itemBuilderKJS.glow) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void getUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.useDuration != null) {
			ci.setReturnValue(itemBuilderKJS.useDuration.applyAsInt(new ItemStackJS(itemStack)));
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void getUseAnimation(ItemStack itemStack, CallbackInfoReturnable<UseAnim> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.anim != null) {
			ci.setReturnValue(itemBuilderKJS.anim);
		}
	}

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true)
	private void getName(ItemStack itemStack, CallbackInfoReturnable<Component> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.nameGetter != null) {
			ci.setReturnValue(itemBuilderKJS.nameGetter.apply(itemStack));
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.use != null) {
			ItemStack itemStack = player.getItemInHand(hand);
			if (itemBuilderKJS.use.use(level, player, hand)) {
				player.startUsingItem(hand);
				ci.setReturnValue(InteractionResultHolder.consume(player.getItemInHand(hand)));
			} else {
				ci.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		}
	}

	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
	private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.finishUsing != null) {
			ci.setReturnValue(itemBuilderKJS.finishUsing.finishUsingItem(itemStack, level, livingEntity));
		}
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"))
	private void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.releaseUsing != null) {
			itemBuilderKJS.releaseUsing.releaseUsing(itemStack, level, livingEntity, i);
		}
	}
}
