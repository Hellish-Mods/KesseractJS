package dev.latvian.kubejs.registry.types.enchantment;

import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class BasicEnchantment extends Enchantment {
	public final EnchantmentBuilder builder;

	public BasicEnchantment(EnchantmentBuilder b) {
		super(b.rarity, b.category, b.slots);
		builder = b;
	}

	@Override
	public int getMinLevel() {
		return builder.minLevel;
	}

	@Override
	public int getMaxLevel() {
		return builder.maxLevel;
	}

	@Override
	public int getMinCost(int i) {
		if (builder.minCost != null) {
			return builder.minCost.get(i);
		}

		return super.getMinCost(i);
	}

	@Override
	public int getMaxCost(int i) {
		if (builder.maxCost != null) {
			return builder.maxCost.get(i);
		}

		return super.getMaxCost(i);
	}

	@Override
	public int getDamageProtection(int i, DamageSource damageSource) {
		if (builder.damageProtection != null) {
			return builder.damageProtection.getDamageProtection(i, damageSource);
		}

		return super.getDamageProtection(i, damageSource);
	}


	@Override
	public float getDamageBonus(int bonusLevel, MobType mobType) {
		if (builder.damageBonus != null) {
			return builder.damageBonus.getDamageBonus(bonusLevel, UtilsJS.getMobTypeId(mobType));
		}

		return super.getDamageBonus(bonusLevel, mobType);
	}

	@Override
	protected boolean checkCompatibility(Enchantment enchantment) {
		if (enchantment == this) {
			return false;
		} else if (builder.checkCompatibility != null) {
			return builder.checkCompatibility.apply(RegistryInfos.ENCHANTMENT.getId(enchantment));
		}

		return true;
	}

	@Override
	public boolean canEnchant(ItemStack itemStack) {
		if (super.canEnchant(itemStack)) {
			return true;
		} else if (builder.canEnchant != null) {
			return builder.canEnchant.apply(itemStack);
		}

		return false;
	}

	@Override
	public void doPostAttack(LivingEntity entity, Entity target, int level) {
		if (builder.postAttack != null) {
			builder.postAttack.apply(entity, target, level);
		}
	}

	@Override
	public void doPostHurt(LivingEntity entity, Entity target, int level) {
		if (builder.postHurt != null) {
			builder.postHurt.apply(entity, target, level);
		}
	}

	@Override
	public boolean isTreasureOnly() {
		return builder.treasureOnly;
	}

	@Override
	public boolean isCurse() {
		return builder.curse;
	}

	@Override
	public boolean isTradeable() {
		return builder.tradeable;
	}

	@Override
	public boolean isDiscoverable() {
		return builder.discoverable;
	}
}
