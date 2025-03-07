package dev.latvian.kubejs.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;

import static dev.latvian.kubejs.command.ArgumImpl.of;

/**
 * @author ZZZank
 */
@SuppressWarnings("unused")
public interface ArgumentTypeWrapper<T extends ArgumentType<?>, O> {

    T create(CommandRegistryEventJS event);

    O getResult(CommandContext<CommandSourceStack> context, String input) throws CommandSyntaxException;

    // numeric types
    ArgumentTypeWrapper<BoolArgumentType, Boolean> BOOLEAN = of(BoolArgumentType::bool, BoolArgumentType::getBool);
    ArgumentTypeWrapper<FloatArgumentType, Float> FLOAT = of(FloatArgumentType::floatArg, FloatArgumentType::getFloat);
    ArgumentTypeWrapper<DoubleArgumentType, Double> DOUBLE = of(DoubleArgumentType::doubleArg, DoubleArgumentType::getDouble);
    ArgumentTypeWrapper<IntegerArgumentType, Integer> INTEGER = of(IntegerArgumentType::integer, IntegerArgumentType::getInteger);
    ArgumentTypeWrapper<LongArgumentType, Long> LONG = of(LongArgumentType::longArg, LongArgumentType::getLong);
    // string types
    ArgumentTypeWrapper<StringArgumentType, String> STRING = of(StringArgumentType::string, StringArgumentType::getString);
    ArgumentTypeWrapper<StringArgumentType, String>
        GREEDY_STRING = of(StringArgumentType::greedyString, StringArgumentType::getString);
    ArgumentTypeWrapper<StringArgumentType, String> WORD = of(StringArgumentType::word, StringArgumentType::getString);
    // entity / player types
    ArgumentTypeWrapper<EntityArgument, Entity> ENTITY = of(EntityArgument::entity, EntityArgument::getEntity);
    ArgumentTypeWrapper<EntityArgument, Collection<? extends Entity>>
        ENTITIES = of(EntityArgument::entities, EntityArgument::getEntities);
    ArgumentTypeWrapper<EntityArgument, ServerPlayer> PLAYER = of(EntityArgument::player, EntityArgument::getPlayer);
    ArgumentTypeWrapper<EntityArgument, Collection<ServerPlayer>> PLAYERS = of(EntityArgument::players, EntityArgument::getPlayers);
    ArgumentTypeWrapper<GameProfileArgument, Collection<GameProfile>>
        GAME_PROFILE = of(GameProfileArgument::gameProfile, GameProfileArgument::getGameProfiles);
    // position types
    ArgumentTypeWrapper<BlockPosArgument, BlockPos> BLOCK_POS = of(BlockPosArgument::blockPos, BlockPosArgument::getOrLoadBlockPos);
    ArgumentTypeWrapper<BlockPosArgument, BlockPos> BLOCK_POS_LOADED = of(BlockPosArgument::blockPos, BlockPosArgument::getLoadedBlockPos);
    ArgumentTypeWrapper<ColumnPosArgument, ColumnPos> COLUMN_POS = of(ColumnPosArgument::columnPos, ColumnPosArgument::getColumnPos);
    // by default, vector arguments are automatically placed at the **center** of the block
    // if no explicit offset is given, since devs may not necessarily want that, we provide both options
    ArgumentTypeWrapper<Vec3Argument, Vec3> VEC3 = of(() -> Vec3Argument.vec3(false), Vec3Argument::getVec3);
    ArgumentTypeWrapper<Vec2Argument, Vec2> VEC2 = of(() -> new Vec2Argument(false), Vec2Argument::getVec2);
    ArgumentTypeWrapper<Vec3Argument, Vec3> VEC3_CENTERED = of(Vec3Argument::vec3, Vec3Argument::getVec3);
    ArgumentTypeWrapper<Vec2Argument, Vec2> VEC2_CENTERED = of(Vec2Argument::vec2, Vec2Argument::getVec2);
    // block-based types
    ArgumentTypeWrapper<BlockStateArgument, BlockInput> BLOCK_STATE = of(BlockStateArgument::block, BlockStateArgument::getBlock);
    ArgumentTypeWrapper<BlockPredicateArgument, Predicate<BlockInWorld>>
        BLOCK_PREDICATE = of(BlockPredicateArgument::blockPredicate, BlockPredicateArgument::getBlockPredicate);
    // item-based types
    ArgumentTypeWrapper<ItemArgument, ItemInput> ITEM_STACK = of(ItemArgument::item, ItemArgument::getItem);
    ArgumentTypeWrapper<ItemPredicateArgument, Predicate<ItemStack>>
        ITEM_PREDICATE = of(ItemPredicateArgument::itemPredicate, ItemPredicateArgument::getItemPredicate);
    // message / chat types
    ArgumentTypeWrapper<ColorArgument, ChatFormatting> COLOR = of(ColorArgument::color, ColorArgument::getColor);
    ArgumentTypeWrapper<ComponentArgument, Component> COMPONENT = of(ComponentArgument::textComponent, ComponentArgument::getComponent);
    ArgumentTypeWrapper<MessageArgument, Component> MESSAGE = of(MessageArgument::message, MessageArgument::getMessage);
    // nbt
    ArgumentTypeWrapper<CompoundTagArgument, CompoundTag>
        NBT_COMPOUND = of(CompoundTagArgument::compoundTag, CompoundTagArgument::getCompoundTag);
    ArgumentTypeWrapper<NbtTagArgument, Tag> NBT_TAG = of(NbtTagArgument::nbtTag, NbtTagArgument::getNbtTag);
    ArgumentTypeWrapper<NbtPathArgument, NbtPathArgument.NbtPath>
        NBT_PATH = of(NbtPathArgument::nbtPath, NbtPathArgument::getPath);
    // random / misc
    ArgumentTypeWrapper<ParticleArgument, ParticleOptions> PARTICLE = of(ParticleArgument::particle, ParticleArgument::getParticle);
    ArgumentTypeWrapper<AngleArgument, Float> ANGLE = of(AngleArgument::angle, AngleArgument::getAngle);
    ArgumentTypeWrapper<RotationArgument, Coordinates> ROTATION = of(RotationArgument::rotation, RotationArgument::getRotation);
    ArgumentTypeWrapper<SwizzleArgument, EnumSet<Direction.Axis>> SWIZZLE = of(SwizzleArgument::swizzle, SwizzleArgument::getSwizzle);
    ArgumentTypeWrapper<SlotArgument, Integer> ITEM_SLOT = of(SlotArgument::slot, SlotArgument::getSlot);
    ArgumentTypeWrapper<ResourceLocationArgument, ResourceLocation>
        RESOURCE_LOCATION = of(ResourceLocationArgument::id, ResourceLocationArgument::getId);
    ArgumentTypeWrapper<MobEffectArgument, MobEffect> MOB_EFFECT = of(MobEffectArgument::effect, MobEffectArgument::getEffect);
    ArgumentTypeWrapper<EntityAnchorArgument, EntityAnchorArgument.Anchor>
        ENTITY_ANCHOR = of(EntityAnchorArgument::anchor, EntityAnchorArgument::getAnchor);
    ArgumentTypeWrapper<RangeArgument.Ints, MinMaxBounds.Ints> INT_RANGE = of(RangeArgument::intRange, RangeArgument.Ints::getRange);
    ArgumentTypeWrapper<RangeArgument.Floats, RangeArgument.Floats>
        FLOAT_RANGE = of(RangeArgument::floatRange, (context, name) -> context.getArgument(name, RangeArgument.Floats.class));
    ArgumentTypeWrapper<ItemEnchantmentArgument, Enchantment>
        ITEM_ENCHANTMENT = of(ItemEnchantmentArgument::enchantment, ItemEnchantmentArgument::getEnchantment);
    ArgumentTypeWrapper<EntitySummonArgument, ResourceLocation>
        ENTITY_SUMMON = of(EntitySummonArgument::id, EntitySummonArgument::getSummonableEntity);
    ArgumentTypeWrapper<DimensionArgument, ServerLevel> DIMENSION = of(DimensionArgument::dimension, DimensionArgument::getDimension);
    ArgumentTypeWrapper<TimeArgument, Integer> TIME = of(TimeArgument::time, IntegerArgumentType::getInteger);
    ArgumentTypeWrapper<UuidArgument, UUID> UUID = of(UuidArgument::uuid, UuidArgument::getUuid);
}
