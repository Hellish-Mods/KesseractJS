package dev.latvian.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.kubejs.block.events.BlockRegistryEventJS;
import dev.latvian.kubejs.item.events.ItemRegistryEventJS;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.*;

/**
 * @author ZZZank
 */
public interface RegistryInfos {

    /**
     * Add your registry to these to make sure it comes after vanilla registries, if it depends on them.
     * Only works on Fabric, since Forge already has ordered registries.
     */
    LinkedList<RegistryInfo<?>> AFTER_VANILLA = new LinkedList<>();
    Map<ResourceKey<? extends Registry<?>>, RegistryInfo<?>> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<ResourceKey<? extends Registry<?>>, RegistryInfo<?>> WITH_TYPE = Collections.synchronizedMap(new LinkedHashMap<>());
    List<BuilderBase<?>> ALL_BUILDERS = new ArrayList<>();

    RegistryInfo<SoundEvent> SOUND_EVENT = RegistryInfo.of(Registry.SOUND_EVENT, SoundEvent.class);
    RegistryInfo<Fluid> FLUID = RegistryInfo.of(Registry.FLUID, Fluid.class);
    RegistryInfo<MobEffect> MOB_EFFECT = RegistryInfo.of(Registry.MOB_EFFECT, MobEffect.class).languageKeyPrefix("effect");
    RegistryInfo<Block> BLOCK = RegistryInfo.of(Registry.BLOCK, Block.class).customRegistryEvent(BlockRegistryEventJS::new);
    RegistryInfo<Enchantment> ENCHANTMENT = RegistryInfo.of(Registry.ENCHANTMENT, Enchantment.class);
    RegistryInfo<EntityType> ENTITY_TYPE = RegistryInfo.of(Registry.ENTITY_TYPE, EntityType.class);
    RegistryInfo<Item> ITEM = RegistryInfo.of(Registry.ITEM, Item.class).noAutoWrap().customRegistryEvent(ItemRegistryEventJS::new);
    RegistryInfo<Potion> POTION = RegistryInfo.of(Registry.POTION, Potion.class);
    RegistryInfo<ParticleType> PARTICLE_TYPE = RegistryInfo.of(Registry.PARTICLE_TYPE, ParticleType.class);
    RegistryInfo<BlockEntityType> BLOCK_ENTITY_TYPE = RegistryInfo.of(Registry.BLOCK_ENTITY_TYPE, BlockEntityType.class);
    RegistryInfo<ResourceLocation> CUSTOM_STAT = RegistryInfo.of(Registry.CUSTOM_STAT, ResourceLocation.class);
    RegistryInfo<ChunkStatus> CHUNK_STATUS = RegistryInfo.of(Registry.CHUNK_STATUS, ChunkStatus.class);
    RegistryInfo<RuleTestType> RULE_TEST = RegistryInfo.of(Registry.RULE_TEST, RuleTestType.class);
    RegistryInfo<PosRuleTestType> POS_RULE_TEST = RegistryInfo.of(Registry.POS_RULE_TEST, PosRuleTestType.class);
    RegistryInfo<MenuType> MENU = RegistryInfo.of(Registry.MENU, MenuType.class);
    RegistryInfo<RecipeType> RECIPE_TYPE = RegistryInfo.of(Registry.RECIPE_TYPE, RecipeType.class);
    RegistryInfo<RecipeSerializer> RECIPE_SERIALIZER = RegistryInfo.of(Registry.RECIPE_SERIALIZER, RecipeSerializer.class);
    RegistryInfo<Attribute> ATTRIBUTE = RegistryInfo.of(Registry.ATTRIBUTE, Attribute.class);
    RegistryInfo<StatType> STAT_TYPE = RegistryInfo.of(Registry.STAT_TYPE, StatType.class);
    RegistryInfo<VillagerType> VILLAGER_TYPE = RegistryInfo.of(Registry.VILLAGER_TYPE, VillagerType.class);
    RegistryInfo<VillagerProfession> VILLAGER_PROFESSION = RegistryInfo.of(Registry.VILLAGER_PROFESSION, VillagerProfession.class);
    RegistryInfo<PoiType> POINT_OF_INTEREST_TYPE = RegistryInfo.of(Registry.POINT_OF_INTEREST_TYPE, PoiType.class);
    RegistryInfo<MemoryModuleType> MEMORY_MODULE_TYPE = RegistryInfo.of(Registry.MEMORY_MODULE_TYPE, MemoryModuleType.class);
    RegistryInfo<SensorType> SENSOR_TYPE = RegistryInfo.of(Registry.SENSOR_TYPE, SensorType.class);
    RegistryInfo<Schedule> SCHEDULE = RegistryInfo.of(Registry.SCHEDULE, Schedule.class);
    RegistryInfo<Activity> ACTIVITY = RegistryInfo.of(Registry.ACTIVITY, Activity.class);
    RegistryInfo<LootPoolEntryType> LOOT_ENTRY = RegistryInfo.of(Registry.LOOT_POOL_ENTRY_TYPE, LootPoolEntryType.class);
    RegistryInfo<LootItemFunctionType> LOOT_FUNCTION = RegistryInfo.of(Registry.LOOT_FUNCTION_TYPE, LootItemFunctionType.class);
    RegistryInfo<LootItemConditionType> LOOT_ITEM = RegistryInfo.of(Registry.LOOT_CONDITION_TYPE, LootItemConditionType.class);
    RegistryInfo<DimensionType> DIMENSION_TYPE = RegistryInfo.of(Registry.DIMENSION_TYPE_REGISTRY, DimensionType.class);
    RegistryInfo<Level> DIMENSION = RegistryInfo.of(Registry.DIMENSION_REGISTRY, Level.class);
    RegistryInfo<LevelStem> LEVEL_STEM = RegistryInfo.of(Registry.LEVEL_STEM_REGISTRY, LevelStem.class);
    RegistryInfo<NoiseGeneratorSettings> NOISE_GENERATOR_SETTINGS = RegistryInfo.of(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.class);
    RegistryInfo<ConfiguredWorldCarver> CONFIGURED_CARVER = RegistryInfo.of(Registry.CONFIGURED_CARVER_REGISTRY, ConfiguredWorldCarver.class);
    RegistryInfo<ConfiguredFeature> CONFIGURED_FEATURE = RegistryInfo.of(Registry.CONFIGURED_FEATURE_REGISTRY, ConfiguredFeature.class);
    RegistryInfo<StructureProcessorList> PROCESSOR_LIST = RegistryInfo.of(Registry.PROCESSOR_LIST_REGISTRY, StructureProcessorList.class);
    RegistryInfo<StructureTemplatePool> TEMPLATE_POOL = RegistryInfo.of(Registry.TEMPLATE_POOL_REGISTRY, StructureTemplatePool.class);
    RegistryInfo<Biome> BIOME = RegistryInfo.of(Registry.BIOME_REGISTRY, Biome.class);
    RegistryInfo<WorldCarver> CARVER = RegistryInfo.of(Registry.CARVER, WorldCarver.class);
    RegistryInfo<Feature> FEATURE = RegistryInfo.of(Registry.FEATURE, Feature.class);
    RegistryInfo<StructurePieceType> STRUCTURE_PIECE = RegistryInfo.of(Registry.STRUCTURE_PIECE, StructurePieceType.class);
    RegistryInfo<BlockStateProviderType> BLOCK_STATE_PROVIDER_TYPE = RegistryInfo.of(Registry.BLOCK_STATE_PROVIDER_TYPE_REGISTRY, BlockStateProviderType.class);
    RegistryInfo<FoliagePlacerType> FOLIAGE_PLACER_TYPE = RegistryInfo.of(Registry.FOLIAGE_PLACER_TYPES, FoliagePlacerType.class);
    RegistryInfo<TrunkPlacerType> TRUNK_PLACER_TYPE = RegistryInfo.of(Registry.TRUNK_PLACER_TYPES, TrunkPlacerType.class);
    RegistryInfo<TreeDecoratorType> TREE_DECORATOR_TYPE = RegistryInfo.of(Registry.TREE_DECORATOR_TYPES, TreeDecoratorType.class);
    RegistryInfo<BlockPlacerType> BLOCK_PLACER_TYPE = RegistryInfo.of(Registry.BLOCK_PLACER_TYPE_REGISTRY, BlockPlacerType.class);
    RegistryInfo<FeatureSizeType> FEATURE_SIZE_TYPE = RegistryInfo.of(Registry.FEATURE_SIZE_TYPES, FeatureSizeType.class);
    RegistryInfo<Codec> BIOME_SOURCE = RegistryInfo.of(Registry.BIOME_SOURCE, Codec.class);
    RegistryInfo<Codec> CHUNK_GENERATOR = RegistryInfo.of(Registry.CHUNK_GENERATOR, Codec.class);
    RegistryInfo<StructureProcessorType> STRUCTURE_PROCESSOR = RegistryInfo.of(Registry.STRUCTURE_PROCESSOR, StructureProcessorType.class);
    RegistryInfo<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = RegistryInfo.of(Registry.STRUCTURE_POOL_ELEMENT, StructurePoolElementType.class);
    //	public static final RegistryInfo<BannerPattern> BANNER_PATTERN = of(Registry.BANNER_PATTERN, BannerPattern.class);
    //	public static final RegistryInfo<CreativeModeTab> CREATIVE_MODE_TAB = of(Registry.CREATIVE_MODE_TAB, CreativeModeTab.class);
    RegistryInfo<LootItemConditionType> LOOT_CONDITION_TYPE = RegistryInfo.of(Registry.LOOT_CONDITION_TYPE, LootItemConditionType.class);
    RegistryInfo<LootItemFunctionType> LOOT_FUNCTION_TYPE = RegistryInfo.of(Registry.LOOT_FUNCTION_TYPE, LootItemFunctionType.class);
    RegistryInfo<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = RegistryInfo.of(Registry.LOOT_POOL_ENTRY_TYPE, LootPoolEntryType.class);
    RegistryInfo<NoiseGeneratorSettings> NOISE_SETTINGS = RegistryInfo.of(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.class);
}
