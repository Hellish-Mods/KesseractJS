package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.documentation.P;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class EntityPotionEffectsJS
{
	private final EntityLivingBase entity;

	public EntityPotionEffectsJS(EntityLivingBase e)
	{
		entity = e;
	}

	public void clear()
	{
		entity.clearActivePotions();
	}

	public Collection<PotionEffect> getActive()
	{
		return entity.getActivePotionEffects();
	}

	public Map<Potion, PotionEffect> getMap()
	{
		return entity.getActivePotionMap();
	}

	public boolean isActive(@P("potion") Potion potion)
	{
		return entity.isPotionActive(potion);
	}

	@Nullable
	public PotionEffect getActive(@P("potion") Potion potion)
	{
		return entity.getActivePotionEffect(potion);
	}

	public void add(@P("effect") PotionEffect effect)
	{
		entity.addPotionEffect(effect);
	}

	public boolean isApplicable(@P("effect") PotionEffect effect)
	{
		return entity.isPotionApplicable(effect);
	}
}