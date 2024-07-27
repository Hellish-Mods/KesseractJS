package dev.latvian.kubejs.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.bindings.RarityWrapper;
import dev.latvian.kubejs.core.ItemKJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.item.custom.*;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.registry.types.tab.KjsTabs;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class ItemBuilder extends BuilderBase<Item> {
	public static final Map<String, Tier> TOOL_TIERS = new HashMap<>();
	public static final Map<String, ArmorMaterial> ARMOR_TIERS = new HashMap<>();

	static {
		for (Tier tier : Tiers.values()) {
			TOOL_TIERS.put(tier.toString().toLowerCase(), tier);
		}
		for (ArmorMaterial tier : ArmorMaterials.values()) {
			ARMOR_TIERS.put(tier.toString().toLowerCase(), tier);
		}
	}

	public transient final List<Component> tooltip;
	public transient ItemType type = BasicItemType.INSTANCE;
	public transient int maxStackSize;
	public transient int maxDamage;
	public transient int burnTime;
	public transient String containerItem;
	public transient Function<ItemStackJS, Collection<ItemStackJS>> subtypes;
	public transient Map<ToolType, Integer> tools;
	public transient float miningSpeed;
	public transient Float attackDamage;
	public transient Float attackSpeed;
	public transient RarityWrapper rarity;
	public transient boolean glow;
	public transient CreativeModeTab group;
	@Deprecated
	public transient Int2IntOpenHashMap color;
	public transient boolean fireResistant;
	@Nullable
	public transient ItemTintFunction tint;
	public transient ItemBuilder.NameCallback nameGetter;
	public transient Multimap<ResourceLocation, AttributeModifier> attributes;
	public transient UseAnim anim;
	public transient ToIntFunction<ItemStackJS> useDuration;
	public transient ItemBuilder.UseCallback use;
	public transient ItemBuilder.FinishUsingCallback finishUsing;
	public transient ItemBuilder.ReleaseUsingCallback releaseUsing;
	public String texture;
	public String parentModel;
	public transient FoodBuilder foodBuilder;
	/**
	 * @see BuilderBase#tags
	 */
	@Deprecated
	public transient Set<String> defaultTags;
	/**
	 * @see BuilderBase#object
	 */
	@Deprecated
	public Item item;

	public JsonObject textureJson;

	//tools related, kept for backward compatibility
	public transient Tier toolTier;
	public transient float attackDamageBaseline;
	public transient float attackSpeedBaseline;

	//armor related, kept for backward compatibility
	public transient ArmorMaterial armorTier;

	public JsonObject modelJson;

	public ItemBuilder(ResourceLocation i) {
		super(i);
		maxStackSize = 64;
		maxDamage = 0;
		burnTime = 0;
		containerItem = "minecraft:air";
		subtypes = null;
		rarity = RarityWrapper.COMMON;
		glow = false;
		tooltip = new ArrayList<>();
		textureJson = new JsonObject();
		parentModel = "";
		foodBuilder = null;
		modelJson = null;
		attributes = ArrayListMultimap.create();
		anim = null;
		useDuration = null;
		use = null;
		finishUsing = null;
		releaseUsing = null;
		fireResistant = false;
		group = KubeJS.tab;
	}

	public static ArmorMaterial toArmorMaterial(Object o) {
		if (o instanceof ArmorMaterial armorMaterial) {
			return armorMaterial;
		}
		String asString = String.valueOf(o);
		ArmorMaterial armorMaterial = ItemBuilder.ARMOR_TIERS.get(asString);
		if (armorMaterial != null) {
			return armorMaterial;
		}
		String withKube = KubeJS.appendModId(asString);
		return ItemBuilder.ARMOR_TIERS.getOrDefault(withKube, ArmorMaterials.IRON);
	}


	@ExpectPlatform
	private static void appendToolType(Item.Properties properties, ToolType type, Integer level) {
		throw new AssertionError();
	}

	public static Tier toToolTier(Object o) {
		if (o instanceof Tier tier) {
			return tier;
		}

		String asString = String.valueOf(o);

		Tier toolTier = ItemBuilder.TOOL_TIERS.get(asString);
		if (toolTier != null) {
			return toolTier;
		}

		String withKube = KubeJS.appendModId(asString);
		return ItemBuilder.TOOL_TIERS.getOrDefault(withKube, Tiers.IRON);
	}

	@Override
	public final RegistryInfo<Item> getRegistryType() {
		return RegistryInfos.ITEM;
	}

	@Override
	public Item createObject() {
		return type.createItem(this);
	}

	@Override
	public Item transformObject(Item obj) {
		((ItemKJS) obj).setItemBuilderKJS(this);
		return obj;
	}

	public ItemBuilder type(ItemType t) {
		type = t;
		type.applyDefaults(this);
		return this;
	}

	public ItemBuilder tier(String t) {
		if (type == BasicItemType.INSTANCE) {
			return this;
		} else if (type instanceof ArmorItemType) {
			armorTier = ARMOR_TIERS.getOrDefault(t, ArmorMaterials.IRON);
			return this;
		}

		toolTier = TOOL_TIERS.getOrDefault(t, Tiers.IRON);
		return this;
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		if (modelJson != null) {
			generator.json(AssetJsonGenerator.asItemModelLocation(id), modelJson);
			return;
		}
		generator.itemModel(id, m -> {
			m.parent(parentModel.isEmpty() ? "minecraft:item/generated" : parentModel);

			if (textureJson.size() == 0) {
				texture(newID("item/", "").toString());
			}

			m.textures(textureJson);
		});
	}

	@JSInfo("Sets the item's max stack size. Default is 64.")
	public ItemBuilder maxStackSize(int v) {
		maxStackSize = v;
		return this;
	}

	@JSInfo("Makes the item not stackable, equivalent to setting the item's max stack size to 1.")
	public ItemBuilder unstackable() {
		return maxStackSize(1);
	}

	@JSInfo("Sets the item's max damage. Default is 0 (No durability).")
	public ItemBuilder maxDamage(int v) {
		maxDamage = v;
		return this;
	}

	@JSInfo("Sets the item's burn time. Default is 0 (Not a fuel).")
	public ItemBuilder burnTime(int v) {
		burnTime = v;
		return this;
	}

	@JSInfo("Sets the item's container item, e.g. a bucket for a milk bucket.")
	public ItemBuilder containerItem(String id) {
		containerItem = id;
		return this;
	}

	@JSInfo("""
			Adds subtypes to the item. The function should return a collection of item stacks, each with a different subtype.

			Each subtype will appear as a separate item in JEI and the creative inventory.
			""")
	public ItemBuilder subtypes(Function<ItemStackJS, Collection<ItemStackJS>> fn) {
		subtypes = fn;
		return this;
	}

	public ItemBuilder tool(ToolType type, int level) {
		tools.put(type, level);
		return this;
	}

	public ItemBuilder miningSpeed(float f) {
		miningSpeed = f;
		ConsoleJS.STARTUP.warn("You should be using a 'pickaxe' or other tool type item if you want to modify mining speed!");
		return this;
	}

	public ItemBuilder attackDamage(float f) {
		attackDamage = f;
		ConsoleJS.STARTUP.warn("You should be using a 'sword' type item if you want to modify attack damage!");
		return this;
	}

	public ItemBuilder attackSpeed(float f) {
		attackSpeed = f;
		ConsoleJS.STARTUP.warn("You should be using a 'sword' type item if you want to modify attack speed!");
		return this;
	}

	@JSInfo("Sets the item's rarity.")
	public ItemBuilder rarity(RarityWrapper v) {
		rarity = v;
		return this;
	}

	@JSInfo("Makes the item glow like enchanted, even if it's not enchanted.")
	public ItemBuilder glow(boolean v) {
		glow = v;
		return this;
	}

	@JSInfo("Adds a tooltip to the item.")
	public ItemBuilder tooltip(Component text) {
		tooltip.add(text);
		return this;
	}

	public ItemBuilder group(String groupId) {
		var tab = KjsTabs.get(groupId);
		if (tab != null) {
			this.group = tab;
		}
		return this;
	}

	@JSInfo("Colorizes item's texture of the given index. Index is used when you have multiple layers, e.g. a crushed ore (of rock + ore).")
	public ItemBuilder color(int index, ItemTintFunction color) {
		if (!(tint instanceof ItemTintFunction.Mapped mapped)) {
			tint = new ItemTintFunction.Mapped();
		}
		((ItemTintFunction.Mapped) tint).map.put(index, color);
		return this;
	}

	@JSInfo("Colorizes item's texture. Useful for coloring items, like GT ores ore dusts.")
	public ItemBuilder color(ItemTintFunction callback) {
		tint = callback;
		return this;
	}

	@JSInfo("Sets the item's texture (layer0).")
	public ItemBuilder texture(String tex) {
		textureJson.addProperty("layer0", tex);
		return this;
	}

	@JSInfo("Sets the item's texture by given key.")
	public ItemBuilder texture(String key, String tex) {
		textureJson.addProperty(key, tex);
		return this;
	}

	@JSInfo("Directlys set the item's texture json.")
	public ItemBuilder textureJson(JsonObject json) {
		textureJson = json;
		return this;
	}

	@JSInfo("Directly set the item's model json.")
	public ItemBuilder modelJson(JsonObject json) {
		modelJson = json;
		return this;
	}

	@JSInfo("Sets the item's model (parent).")
	public ItemBuilder parentModel(String m) {
		parentModel = m;
		return this;
	}

	@JSInfo("""
			Sets the item's name dynamically.
			""")
	public ItemBuilder name(ItemBuilder.NameCallback name) {
		this.nameGetter = name;
		return this;
	}

	@JSInfo("""
			Set the food properties of the item.
			""")
	public ItemBuilder food(Consumer<FoodBuilder> b) {
		foodBuilder = new FoodBuilder();
		b.accept(foodBuilder);
		return this;
	}

	public Map<ToolType, Integer> getToolsMap() {
		return tools;
	}

	public float getMiningSpeed() {
		return miningSpeed;
	}

	@Nullable
	public Float getAttackDamage() {
		return attackDamage;
	}

	@Nullable
	public Float getAttackSpeed() {
		return attackSpeed;
	}

	@JSInfo("Makes the item fire resistant like netherite tools (or not).")
	public ItemBuilder fireResistant(boolean isFireResistant) {
		fireResistant = isFireResistant;
		return this;
	}

	@JSInfo("Makes the item fire resistant like netherite tools.")
	public ItemBuilder fireResistant() {
		return fireResistant(true);
	}

	public Item.Properties createItemProperties() {
		var properties = new KubeJSItemProperties(this);

		properties.tab(group);

		if (maxDamage > 0) {
			properties.durability(maxDamage);
		} else {
			properties.stacksTo(maxStackSize);
		}

		properties.rarity(rarity.rarity);

		if (tools != null) {
			for (var entry : tools.entrySet()) {
				appendToolType(properties, entry.getKey(), entry.getValue());
			}
		}

		Item item = KubeJSRegistries.items().get(new ResourceLocation(containerItem));

		if (item != Items.AIR) {
			properties.craftRemainder(item);
		}

		if (foodBuilder != null) {
			properties.food(foodBuilder.build());
		}

		if (fireResistant) {
			properties.fireResistant();
		}

		return properties;
	}

	@JSInfo(value = """
			Adds an attribute modifier to the item.

			An attribute modifier is something like a damage boost or a speed boost.
			On tools, they're applied when the item is held, on armor, they're
			applied when the item is worn.
			"""
//			, params = {
//					@JSParam(rename = "attribute", value = "The resource location of the attribute, e.g. 'generic.attack_damage'"),
//					@JSParam(rename = "identifier", value = "A unique identifier for the modifier. Modifiers are considered the same if they have the same identifier."),
//					@JSParam(rename = "d", value = "The amount of the modifier."), @JSParam(rename = "operation", value = "The operation to apply the modifier with. Can be ADDITION, MULTIPLY_BASE, or MULTIPLY_TOTAL.")}
	)
	public ItemBuilder modifyAttribute(ResourceLocation attribute, String identifier, double d, AttributeModifier.Operation operation) {
		attributes.put(attribute, new AttributeModifier(new UUID(identifier.hashCode(), identifier.hashCode()), identifier, d, operation));
		return this;
	}

	@JSInfo("Determines the animation of the item when used, e.g. eating food.")
	public ItemBuilder useAnimation(UseAnim animation) {
		this.anim = animation;
		return this;
	}

	@JSInfo("""
			The duration when the item is used.

			For example, when eating food, this is the time it takes to eat the food.
			This can change the eating speed, or be used for other things (like making a custom bow).
			""")
	public ItemBuilder useDuration(ToIntFunction<ItemStackJS> useDuration) {
		this.useDuration = useDuration;
		return this;
	}

	@JSInfo("""
			Determines if player will start using the item.

			For example, when eating food, returning true will make the player start eating the food.
			""")
	public ItemBuilder use(ItemBuilder.UseCallback use) {
		this.use = use;
		return this;
	}

	@JSInfo("""
			When players finish using the item.

			This is called only when `useDuration` ticks have passed.

			For example, when eating food, this is called when the player has finished eating the food, so hunger is restored.
			""")
	public ItemBuilder finishUsing(ItemBuilder.FinishUsingCallback finishUsing) {
		this.finishUsing = finishUsing;
		return this;
	}

	@JSInfo("""
			When players did not finish using the item but released the right mouse button halfway through.

			An example is the bow, where the arrow is shot when the player releases the right mouse button.

			To ensure the bow won't finish using, Minecraft sets the `useDuration` to a very high number (1h).
			""")
	public ItemBuilder releaseUsing(ItemBuilder.ReleaseUsingCallback releaseUsing) {
		this.releaseUsing = releaseUsing;
		return this;
	}

	@FunctionalInterface
	public interface UseCallback {
		boolean use(Level level, Player player, InteractionHand interactionHand);
	}

	@FunctionalInterface
	public interface FinishUsingCallback {
		ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity);
	}

	@FunctionalInterface
	public interface ReleaseUsingCallback {
		void releaseUsing(ItemStack itemStack, Level level, LivingEntity user, int tick);
	}

	@FunctionalInterface
	public interface NameCallback {
		Component apply(ItemStack itemStack);
	}
}