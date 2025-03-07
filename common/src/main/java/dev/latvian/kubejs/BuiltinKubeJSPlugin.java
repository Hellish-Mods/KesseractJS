package dev.latvian.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import dev.latvian.kubejs.bindings.*;
import dev.latvian.kubejs.block.*;
import dev.latvian.kubejs.block.custom.*;
import dev.latvian.kubejs.block.custom.builder.*;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.client.painter.Painter;
import dev.latvian.kubejs.client.painter.screen.*;
import dev.latvian.kubejs.client.toast.NotificationBuilder;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.event.IEventHandler;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.fluid.FluidWrapper;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.generator.DataJsonGenerator;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ItemTintFunction;
import dev.latvian.kubejs.item.custom.*;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.kubejs.recipe.ingredientaction.IngredientActionFilter;
import dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.SmithingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
import dev.latvian.kubejs.recipe.mod.AE2GrinderRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantingApparatusRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauEnchantmentRecipeJS;
import dev.latvian.kubejs.recipe.mod.ArsNouveauGlyphPressRecipeJS;
import dev.latvian.kubejs.recipe.mod.BotaniaRunicAltarRecipeJS;
import dev.latvian.kubejs.recipe.mod.BotanyPotsCropRecipeJS;
import dev.latvian.kubejs.recipe.mod.IDSqueezerRecipeJS;
import dev.latvian.kubejs.recipe.mod.MATagRecipeJS;
import dev.latvian.kubejs.recipe.mod.ShapedArtisanRecipeJS;
import dev.latvian.kubejs.recipe.mod.ShapelessArtisanRecipeJS;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.registry.types.SoundEventBuilder;
import dev.latvian.kubejs.registry.types.CustomStatBuilder;
import dev.latvian.kubejs.registry.types.enchantment.EnchantmentBuilder;
import dev.latvian.kubejs.registry.types.mobeffects.BasicMobEffect;
import dev.latvian.kubejs.registry.types.particle.ParticleTypeBuilder;
import dev.latvian.kubejs.registry.types.potion.PotionBuilder;
import dev.latvian.kubejs.registry.types.villagers.PoiTypeBuilder;
import dev.latvian.kubejs.registry.types.villagers.VillagerProfessionBuilder;
import dev.latvian.kubejs.registry.types.villagers.VillagerTypeBuilder;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.PlatformWrapper;
import dev.latvian.kubejs.script.RegistryTypeWrapperFactory;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.*;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.mods.rhino.mod.util.NBTWrapper;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.AABBWrapper;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import dev.latvian.mods.rhino.mod.wrapper.DirectionWrapper;
import dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.ToolType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class BuiltinKubeJSPlugin extends KubeJSPlugin {
	public static final HashMap<String, Object> GLOBAL = new HashMap<>();

	@Override
	public void init() {
		BlockTypes.register(BasicBlockType.INSTANCE);
		BlockTypes.register(ShapedBlockType.SLAB);
		BlockTypes.register(ShapedBlockType.STAIRS);
		BlockTypes.register(ShapedBlockType.FENCE);
		BlockTypes.register(ShapedBlockType.FENCE_GATE);
		BlockTypes.register(ShapedBlockType.WALL);
		BlockTypes.register(ShapedBlockType.WOODEN_PRESSURE_PLATE);
		BlockTypes.register(ShapedBlockType.STONE_PRESSURE_PLATE);
		BlockTypes.register(ShapedBlockType.WOODEN_BUTTON);
		BlockTypes.register(ShapedBlockType.STONE_BUTTON);

		ItemTypes.register(BasicItemType.INSTANCE);
		ItemTypes.register(ToolItemType.SWORD);
		ItemTypes.register(ToolItemType.PICKAXE);
		ItemTypes.register(ToolItemType.AXE);
		ItemTypes.register(ToolItemType.SHOVEL);
		ItemTypes.register(ToolItemType.HOE);
		ItemTypes.register(ArmorItemType.HELMET);
		ItemTypes.register(ArmorItemType.CHESTPLATE);
		ItemTypes.register(ArmorItemType.LEGGINGS);
		ItemTypes.register(ArmorItemType.BOOTS);

		//sound
		RegistryInfos.SOUND_EVENT.addType("basic", SoundEventBuilder.class, SoundEventBuilder::new);
        //sound: backward compat
        RegistryInfos.SOUND_EVENT.eventIds.add(KubeJSEvents.SOUND_REGISTRY);
		//block
		RegistryInfos.BLOCK.addType("basic", BlockBuilder.class, BlockBuilder::new);
		RegistryInfos.BLOCK.addType("detector", DetectorBlock.Builder.class, DetectorBlock.Builder::new);
		RegistryInfos.BLOCK.addType("slab", SlabBlockBuilder.class, SlabBlockBuilder::new);
		RegistryInfos.BLOCK.addType("stairs", StairBlockBuilder.class, StairBlockBuilder::new);
		RegistryInfos.BLOCK.addType("fence", FenceBlockBuilder.class, FenceBlockBuilder::new);
		RegistryInfos.BLOCK.addType("wall", WallBlockBuilder.class, WallBlockBuilder::new);
		RegistryInfos.BLOCK.addType("fence_gate", FenceGateBlockBuilder.class, FenceGateBlockBuilder::new);
        RegistryInfos.BLOCK.addType("stone_pressure_plate", PressurePlateBlockBuilder.Stone.class, PressurePlateBlockBuilder.Stone::new);
        RegistryInfos.BLOCK.addType("stone_button", ButtonBlockBuilder.Stone.class, ButtonBlockBuilder.Stone::new);
        RegistryInfos.BLOCK.addType("wooden_pressure_plate", PressurePlateBlockBuilder.Wooden.class, PressurePlateBlockBuilder.Wooden::new);
        RegistryInfos.BLOCK.addType("wooden_button", ButtonBlockBuilder.Wooden.class, ButtonBlockBuilder.Wooden::new);
        RegistryInfos.BLOCK.addType("pressure_plate", PressurePlateBlockBuilder.class, PressurePlateBlockBuilder::new);
        RegistryInfos.BLOCK.addType("button", ButtonBlockBuilder.class, ButtonBlockBuilder::new);
		RegistryInfos.BLOCK.addType("falling", FallingBlockBuilder.class, FallingBlockBuilder::new);
		RegistryInfos.BLOCK.addType("crop", CropBlockBuilder.class, CropBlockBuilder::new);
		RegistryInfos.BLOCK.addType("cardinal", HorizontalDirectionalBlockBuilder.class, HorizontalDirectionalBlockBuilder::new);
		//item
		RegistryInfos.ITEM.addType("basic", ItemBuilder.class, ItemBuilder::new);
		RegistryInfos.ITEM.addType("sword", CustomItemBuilderProxy.Sword.class, CustomItemBuilderProxy.Sword::new);
		RegistryInfos.ITEM.addType("pickaxe", CustomItemBuilderProxy.Pickaxe.class, CustomItemBuilderProxy.Pickaxe::new);
		RegistryInfos.ITEM.addType("axe", CustomItemBuilderProxy.Axe.class, CustomItemBuilderProxy.Axe::new);
		RegistryInfos.ITEM.addType("shovel", CustomItemBuilderProxy.Shovel.class, CustomItemBuilderProxy.Shovel::new);
		RegistryInfos.ITEM.addType("shears", ShearsItemBuilder.class, ShearsItemBuilder::new);
		RegistryInfos.ITEM.addType("hoe", CustomItemBuilderProxy.Hoe.class, CustomItemBuilderProxy.Hoe::new);
		RegistryInfos.ITEM.addType("helmet", CustomItemBuilderProxy.Helmet.class, CustomItemBuilderProxy.Helmet::new);
		RegistryInfos.ITEM.addType("chestplate", CustomItemBuilderProxy.Chestplate.class, CustomItemBuilderProxy.Chestplate::new);
		RegistryInfos.ITEM.addType("leggings", CustomItemBuilderProxy.Leggings.class, CustomItemBuilderProxy.Leggings::new);
		RegistryInfos.ITEM.addType("boots", CustomItemBuilderProxy.Boots.class, CustomItemBuilderProxy.Boots::new);
		RegistryInfos.ITEM.addType("music_disc", RecordItemJS.Builder.class, RecordItemJS.Builder::new);
		//misc
		RegistryInfos.FLUID.addType("basic", FluidBuilder.class, FluidBuilder::new);
		RegistryInfos.ENCHANTMENT.addType("basic", EnchantmentBuilder.class, EnchantmentBuilder::new);
		RegistryInfos.MOB_EFFECT.addType("basic", BasicMobEffect.Builder.class, BasicMobEffect.Builder::new);
		RegistryInfos.POTION.addType("basic", PotionBuilder.class, PotionBuilder::new);
		RegistryInfos.PARTICLE_TYPE.addType("basic", ParticleTypeBuilder.class, ParticleTypeBuilder::new);
		RegistryInfos.CUSTOM_STAT.addType("basic", CustomStatBuilder.class, CustomStatBuilder::new);
		RegistryInfos.POINT_OF_INTEREST_TYPE.addType("basic", PoiTypeBuilder.class, PoiTypeBuilder::new);
		RegistryInfos.VILLAGER_TYPE.addType("basic", VillagerTypeBuilder.class, VillagerTypeBuilder::new);
		RegistryInfos.VILLAGER_PROFESSION.addType("basic", VillagerProfessionBuilder.class, VillagerProfessionBuilder::new);
		//TODO: ENTITY_TYPE
		//TODO: BLOCK_ENTITY_TYPE
	}

	@Override
	public void initStartup() {
		new ItemToolTierEventJS().post(KubeJSEvents.ITEM_REGISTRY_TOOL_TIERS);
		new ItemArmorTierEventJS().post(KubeJSEvents.ITEM_REGISTRY_ARMOR_TIERS);

		for (val registryInfo : RegistryInfos.MAP.values()) {
			registryInfo.fireRegistryEvent();
		}
//		new BlockRegistryEventJS().post(KubeJSEvents.BLOCK_REGISTRY);
//		new ItemRegistryEventJS().post(KubeJSEvents.ITEM_REGISTRY);
//		new FluidRegistryEventJS().post(KubeJSEvents.FLUID_REGISTRY);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void clientInit() {
		Painter.INSTANCE.registerObject("screen_group", ScreenGroup::new);
		Painter.INSTANCE.registerObject("rectangle", RectangleObject::new);
		Painter.INSTANCE.registerObject("text", TextObject::new);
		Painter.INSTANCE.registerObject("atlas_texture", AtlasTextureObject::new);
		Painter.INSTANCE.registerObject("gradient", GradientObject::new);
        Painter.INSTANCE.registerObject("item", ItemObject::new);
	}

	@Override
	public void addClasses(ScriptType type, ClassFilter filter) {
		filter.allow("java.lang.Number"); // java.lang
		filter.allow("java.lang.String");
		filter.allow("java.lang.Character");
		filter.allow("java.lang.Byte");
		filter.allow("java.lang.Short");
		filter.allow("java.lang.Integer");
		filter.allow("java.lang.Long");
		filter.allow("java.lang.Float");
		filter.allow("java.lang.Double");
		filter.allow("java.lang.Boolean");
		filter.allow("java.lang.Runnable");
		filter.allow("java.lang.Iterable");
		filter.allow("java.lang.Comparable");
		filter.allow("java.lang.CharSequence");

		filter.allow("java.math.BigInteger"); // java.math
		filter.allow("java.math.BigDecimal");

		filter.deny("java.io"); // IO
		filter.allow("java.io.Closeable");
		filter.allow("java.io.Serializable");

		filter.deny("java.nio"); // NIO
		filter.allow("java.nio.ByteOrder");

		filter.allow("java.util"); // Utils
		filter.deny("java.util.jar");
		filter.deny("java.util.zip");

		filter.allow("it.unimi.dsi.fastutil"); // FastUtil

		filter.allow("dev.latvian.kubejs"); // KubeJS
		filter.deny("dev.latvian.kubejs.script");
		filter.deny("dev.latvian.kubejs.mixin");
		filter.deny(KubeJSPlugin.class);
		filter.deny(KubeJSPlugins.class);

		filter.allow("net.minecraft"); // Minecraft
		filter.allow("com.mojang.authlib.GameProfile");
		filter.allow("com.mojang.util.UUIDTypeAdapter");
		filter.allow("com.mojang.brigadier");
		filter.allow("com.mojang.blaze3d");

		filter.allow("me.shedaniel.architectury"); // Architectury

		// Misc
		filter.deny("java.net"); // Networks
		filter.deny("sun"); // Sun
		filter.deny("com.sun"); // Sun
		filter.deny("io.netty"); // Netty
		filter.deny("org.objectweb.asm"); // ASM
		filter.deny("org.spongepowered.asm"); // Sponge ASM
		filter.deny("org.openjdk.nashorn"); // Nashorn
		filter.deny("jdk.nashorn"); // Nashorn

		// Mods
		filter.allow("mezz.jei"); // JEI
	}

	@Override
	public void addBindings(BindingsEvent event) {
		event.add("global", GLOBAL);

		if (event.type == ScriptType.SERVER) {
			ServerSettings.instance = new ServerSettings();
			event.add("settings", ServerSettings.instance);
		}

		event.add("Platform", PlatformWrapper.class);
		event.add("console", event.type.console);
		event.add("events", new ScriptEventsWrapper(event.type.manager.get().events));

		event.addFunction("onEvent", args -> onEvent(event, args), String.class, IEventHandler.class);
		event.addFunction("java", args -> event.manager.loadJavaClass(event.scope, args), String.class);

		event.add("JavaMath", Math.class);
        event.add("KMath", KMath.class);
		event.add("ResourceLocation", ResourceLocation.class);

		event.add("Utils", UtilsWrapper.class);
		event.add("utils", UtilsWrapper.class);
		event.add("Text", TextWrapper.class);
		event.add("text", TextWrapper.class);
		event.add("UUID", UUIDWrapper.class);
		event.add("uuid", UUIDWrapper.class);
		event.add("JsonUtils", JsonUtilsJS.class);
		event.add("JsonIO", JsonIOWrapper.class);
		event.add("Block", BlockWrapper.class);
		event.add("block", BlockWrapper.class);
		event.add("Item", ItemWrapper.class);
		event.add("item", ItemWrapper.class);
		event.add("Ingredient", IngredientWrapper.class);
		event.add("ingredient", IngredientWrapper.class);
		event.add("NBT", NBTWrapper.class);
		event.add("NBTIO", NBTIOWrapper.class);
		event.add("Direction", DirectionWrapper.class);
		event.add("Facing", DirectionWrapper.class);
		event.add("AABB", AABBWrapper.class);

		event.add("Fluid", FluidWrapper.class);
		event.add("fluid", FluidWrapper.class);

		event.add("SECOND", 1000L);
		event.add("MINUTE", 60000L);
		event.add("HOUR", 3600000L);

		event.add("Color", ColorWrapper.class);

		event.add("EquipmentSlot", EquipmentSlot.class);
		event.add("SLOT_MAINHAND", EquipmentSlot.MAINHAND);
		event.add("SLOT_OFFHAND", EquipmentSlot.OFFHAND);
		event.add("SLOT_FEET", EquipmentSlot.FEET);
		event.add("SLOT_LEGS", EquipmentSlot.LEGS);
		event.add("SLOT_CHEST", EquipmentSlot.CHEST);
		event.add("SLOT_HEAD", EquipmentSlot.HEAD);

		event.add("Rarity", RarityWrapper.class);
		event.add("RARITY_COMMON", RarityWrapper.COMMON);
		event.add("RARITY_UNCOMMON", RarityWrapper.UNCOMMON);
		event.add("RARITY_RARE", RarityWrapper.RARE);
		event.add("RARITY_EPIC", RarityWrapper.EPIC);

		event.add("AIR_ITEM", Items.AIR);
		event.add("AIR_BLOCK", Blocks.AIR);

		event.add("ToolType", ToolType.class);
		event.add("TOOL_TYPE_AXE", ToolType.AXE);
		event.add("TOOL_TYPE_PICKAXE", ToolType.PICKAXE);
		event.add("TOOL_TYPE_SHOVEL", ToolType.SHOVEL);
		event.add("TOOL_TYPE_HOE", ToolType.HOE);

		event.add("Hand", InteractionHand.class);
		event.add("MAIN_HAND", InteractionHand.MAIN_HAND);
		event.add("OFF_HAND", InteractionHand.OFF_HAND);

		event.add("DecorationGenerationStep", GenerationStep.Decoration.class);
		event.add("CarvingGenerationStep", GenerationStep.Carving.class);
		//vector
		event.add("Vec3", Vec3.class);
		event.add("Vec3d", Vec3.class);
		event.add("Vec3i", Vec3i.class);
		event.add("Vector3d", Vector3d.class);
		event.add("Vector3f", Vector3f.class);
		event.add("Vector4f", Vector4f.class);
		//matrix
		event.add("Matrix3f", Matrix3f.class);
		event.add("Matrix4f", Matrix4f.class);

        event.add("BlockPos", BlockPos.class);
        event.add("BlockProperties", BlockStateProperties.class);
        event.add("Notification", NotificationBuilder.class);

		KubeJS.PROXY.clientBindings(event);
	}

	private static Object onEvent(BindingsEvent event, Object[] args) {
		for (Object o : ListJS.orSelf(args[0])) {
			event.type.manager.get().events.listen(String.valueOf(o), (IEventHandler) args[1]);
		}

		return null;
	}

	@Override
	public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
		// Java / Minecraft //
		typeWrappers.register(String.class, String::valueOf);
		typeWrappers.register(CharSequence.class, String::valueOf);
		typeWrappers.register(UUID.class, UUIDWrapper::fromString);
		typeWrappers.register(Pattern.class, UtilsJS::parseRegex);
		typeWrappers.register(JsonObject.class, MapJS::json);
		typeWrappers.register(JsonArray.class, ListJS::json);

		typeWrappers.register(ResourceLocation.class, UtilsJS::getMCID);
		typeWrappers.register(ItemStack.class, o -> ItemStackJS.of(o).getItemStack());
		typeWrappers.register(CompoundTag.class, MapJS::isNbt, MapJS::nbt);
		typeWrappers.register(CollectionTag.class, ListJS::nbt);
		typeWrappers.register(ListTag.class, o -> (ListTag) ListJS.nbt(o));
		typeWrappers.register(Component.class, Text::componentOf);
		typeWrappers.register(MutableComponent.class, o -> new TextComponent("").append(Text.componentOf(o)));

		typeWrappers.register(BlockPos.class, o -> {
			if (o instanceof BlockPos) {
				return (BlockPos) o;
			} else if (o instanceof BlockContainerJS) {
				return ((BlockContainerJS) o).getPos();
			} else if (o instanceof List<?> l && l.size() >= 3) {
                return new BlockPos(
                    ((Number) l.get(0)).intValue(),
                    ((Number) l.get(1)).intValue(),
                    ((Number) l.get(2)).intValue()
                );
			}

			return BlockPos.ZERO;
		});

		typeWrappers.register(Vec3.class, o -> {
			if (o instanceof Vec3 v) {
				return v;
			} else if (o instanceof EntityJS ent) {
				return ent.minecraftEntity.position();
			} else if (o instanceof List<?> l && l.size() >= 3) {
                return new Vec3(
                    ((Number) l.get(0)).doubleValue(),
                    ((Number) l.get(1)).doubleValue(),
                    ((Number) l.get(2)).doubleValue()
                );
            } else if (o instanceof BlockPos bp) {
                return new Vec3(bp.getX() + 0.5D, bp.getY() + 0.5D, bp.getZ() + 0.5D);
            } else if (o instanceof BlockContainerJS container) {
				val bp = container.getPos();
				return new Vec3(bp.getX() + 0.5D, bp.getY() + 0.5D, bp.getZ() + 0.5D);
			}

			return Vec3.ZERO;
		});

		typeWrappers.register(Item.class, ItemStackJS::getRawItem);
		typeWrappers.register(GenerationStep.Decoration.class, o -> o == null || o.toString().isEmpty() ? null : GenerationStep.Decoration.valueOf(o.toString().toUpperCase()));
		typeWrappers.register(MobCategory.class, o -> o == null ? null : MobCategory.byName(o.toString()));
		typeWrappers.register(net.minecraft.network.chat.TextColor.class, o -> {
			if (o instanceof Number n) {
				return net.minecraft.network.chat.TextColor.fromRgb(n.intValue() & 0xFFFFFF);
			} else if (o instanceof ChatFormatting chatFormatting) {
				return net.minecraft.network.chat.TextColor.fromLegacyFormat(chatFormatting);
			}

			return net.minecraft.network.chat.TextColor.parseColor(o.toString());
		});

		typeWrappers.register(AABB.class, AABBWrapper::wrap);
		typeWrappers.register(Direction.class, o -> o instanceof Direction ? (Direction) o : DirectionWrapper.ALL.get(o.toString().toLowerCase()));
		typeWrappers.register(RandomIntGenerator.class, UtilsJS::randomIntGeneratorOf);
		typeWrappers.register(LootContext.EntityTarget.class, o -> o == null ? null : LootContext.EntityTarget.getByName(o.toString().toLowerCase()));
		typeWrappers.register(CopyNameFunction.NameSource.class, o -> o == null ? null : CopyNameFunction.NameSource.getByName(o.toString().toLowerCase()));

		// KubeJS //
		typeWrappers.register(MapJS.class, MapJS::of);
		typeWrappers.register(ListJS.class, ListJS::of);
		typeWrappers.register(ItemStackJS.class, ItemStackJS::of);
		typeWrappers.register(IngredientJS.class, IngredientJS::of);
		typeWrappers.register(IngredientStackJS.class, o -> IngredientJS.of(o).asIngredientStack());
		typeWrappers.register(Text.class, Text::of);
		typeWrappers.register(BlockStatePredicate.class, BlockStatePredicate::of);
		typeWrappers.register(FluidStackJS.class, FluidStackJS::of);
		typeWrappers.register(RecipeFilter.class, RecipeFilter::of);
		typeWrappers.register(MaterialJS.class, MaterialListJS.INSTANCE::of);
		typeWrappers.register(ItemType.class, ItemTypes::get);
		typeWrappers.register(BlockType.class, BlockTypes::get);
		typeWrappers.register(Color.class, ColorWrapper::of);
		typeWrappers.register(ToolType.class, o -> ToolType.create(o.toString(), () -> null));
		typeWrappers.register(IngredientActionFilter.class, IngredientActionFilter::filterOf);
        //tint
        typeWrappers.register(BlockTintFunction.class, BlockTintFunction::of);
        typeWrappers.register(ItemTintFunction.class, ItemTintFunction::of);

        //registry
        for (val wrapperFactory : RegistryTypeWrapperFactory.getAll()) {
            try {
                typeWrappers.register(wrapperFactory.type, UtilsJS.cast(wrapperFactory));
            } catch (IllegalArgumentException e) {
                KubeJS.LOGGER.error("error when trying to register registry type wrapper: {}", wrapperFactory, e);
            }
        }

		KubeJS.PROXY.clientTypeWrappers(typeWrappers);
	}

	@Override
	public void addRecipes(RegisterRecipeHandlersEvent event) {
		event.registerShaped(KubeJS.id("shaped"));
		event.registerShapeless(KubeJS.id("shapeless"));
		event.registerShaped(new ResourceLocation("minecraft:crafting_shaped"));
		event.registerShapeless(new ResourceLocation("minecraft:crafting_shapeless"));
		event.register(new ResourceLocation("minecraft:stonecutting"), StonecuttingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smelting"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:blasting"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smoking"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:campfire_cooking"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smithing"), SmithingRecipeJS::new);

		// Mod recipe types that use vanilla syntax

		if (Platform.isModLoaded("cucumber")) {
			event.registerShaped(new ResourceLocation("cucumber:shaped_no_mirror"));
		}

		if (Platform.isModLoaded("mysticalagriculture")) {
			event.register(new ResourceLocation("mysticalagriculture:tag"), MATagRecipeJS::new);
		}

		if (Platform.isModLoaded("botanypots")) {
			event.register(new ResourceLocation("botanypots:crop"), BotanyPotsCropRecipeJS::new);
		}

		if (Platform.isModLoaded("extendedcrafting")) {
			event.registerShaped(new ResourceLocation("extendedcrafting:shaped_table"));
			event.registerShapeless(new ResourceLocation("extendedcrafting:shapeless_table"));
		}

		if (Platform.isModLoaded("dankstorage")) {
			event.registerShaped(new ResourceLocation("dankstorage:upgrade"));
		}

		if (Platform.isModLoaded("appliedenergistics2")) {
			event.register(new ResourceLocation("appliedenergistics2:grinder"), AE2GrinderRecipeJS::new);
		}

		if (Platform.isModLoaded("artisanworktables")) {
			String[] types = {
					"basic",
					"blacksmith",
					"carpenter",
					"chef",
					"chemist",
					"designer",
					"engineer",
					"farmer",
					"jeweler",
					"mage",
					"mason",
					"potter",
					"scribe",
					"tailor",
					"tanner"
			};

			for (String t : types) {
				event.register(new ResourceLocation("artisanworktables", t + "_shaped"), ShapedArtisanRecipeJS::new);
				event.register(new ResourceLocation("artisanworktables", t + "_shapeless"), ShapelessArtisanRecipeJS::new);
			}
		}

		if (Platform.isModLoaded("botania")) {
			event.register(new ResourceLocation("botania:runic_altar"), BotaniaRunicAltarRecipeJS::new);
		}

		if (Platform.isModLoaded("integrateddynamics") && !Platform.isModLoaded("kubejs_integrated_dynamics")) {
			event.register(new ResourceLocation("integrateddynamics:squeezer"), IDSqueezerRecipeJS::new);
			event.register(new ResourceLocation("integrateddynamics:mechanical_squeezer"), IDSqueezerRecipeJS::new);
		}

		if (Platform.isModLoaded("ars_nouveau")) {
			event.register(new ResourceLocation("ars_nouveau:enchanting_apparatus"), ArsNouveauEnchantingApparatusRecipeJS::new);
			event.register(new ResourceLocation("ars_nouveau:enchantment"), ArsNouveauEnchantmentRecipeJS::new);
			event.register(new ResourceLocation("ars_nouveau:glyph_recipe"), ArsNouveauGlyphPressRecipeJS::new);
		}
	}

	@Override
	public void generateDataJsons(DataJsonGenerator generator) {
		for (val builder : RegistryInfos.ALL_BUILDERS) {
			builder.generateDataJsons(generator);
		}
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		for (val builder : RegistryInfos.ALL_BUILDERS) {
			builder.generateAssetJsons(generator);
		}
	}

	@Override
	public void generateLang(Map<String, String> enusLang) {
		enusLang.put("itemGroup.kubejs.kubejs", KubeJS.MOD_NAME);

		for (val builder : RegistryInfos.ALL_BUILDERS) {
			if (builder.overrideLangJson && builder.display != null) {
				enusLang.put(builder.getTranslationKey(), builder.display.getString());
			}
		}
	}
}
