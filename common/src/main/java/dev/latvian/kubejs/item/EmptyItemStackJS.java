package dev.latvian.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @see ItemStackJS#EMPTY
 * @author ZZZank
 */
@Deprecated
class EmptyItemStackJS extends ItemStackJS {
    static final ItemStackJS INSTANCE = new EmptyItemStackJS();

    EmptyItemStackJS() {
        super(ItemStack.EMPTY);
    }

    @Override
    public String getId() {
        return "minecraft:air";
    }

    @Override
    public Collection<ResourceLocation> getTags() {
        return Collections.emptySet();
    }

    @Override
    public boolean hasTag(ResourceLocation tag) {
        return false;
    }

    @Override
    public Item getItem() {
        return Items.AIR;
    }

    @Override
    public ItemStackJS copy() {
        return this;
    }

    @Override
    public void setCount(int c) {
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public ItemStackJS withCount(int c) {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    @Nullable
    public CompoundTag getNbt() {
        return null;
    }

    @Override
    public void setNbt(@Nullable CompoundTag tag) {
    }

    @Override
    public boolean hasNBT() {
        return false;
    }

    @Override
    public String getNbtString() {
        return "null";
    }

    @Override
    public ItemStackJS removeNBT() {
        return this;
    }

    @Override
    public ItemStackJS withNBT(CompoundTag nbt) {
        return this;
    }

    @Override
    public void setChance(double c) {
    }

    @Override
    public double getChance() {
        return Double.NaN;
    }

    @Override
    public boolean hasChance() {
        return false;
    }

    public String toString() {
        return "Item.empty";
    }

    @Override
    public boolean test(ItemStackJS other) {
        return false;
    }

    @Override
    public boolean testVanilla(ItemStack other) {
        return false;
    }

    @Override
    public boolean testVanillaItem(Item item) {
        return false;
    }

    @Override
    public Set<ItemStackJS> getStacks() {
        return Collections.emptySet();
    }

    @Override
    public Set<Item> getVanillaItems() {
        return Collections.emptySet();
    }

    @Override
    public ItemStackJS getFirst() {
        return this;
    }

    @Override
    public IngredientJS not() {
        return MatchAllIngredientJS.INSTANCE;
    }

    @Override
    public ItemStackJS withName(@Nullable Component displayName) {
        return this;
    }

    @Override
    public MapJS getEnchantments() {
        return new MapJS() {
            @Override
            protected boolean setChangeListener(@Nullable Object v) {
                return false;
            }
        };
    }

    @Override
    public boolean hasEnchantment(Enchantment enchantment, int level) {
        return false;
    }

    @Override
    public ItemStackJS enchant(MapJS map) {
        return this;
    }

    @Override
    public ItemStackJS enchant(Enchantment enchantment, int level) {
        return this;
    }

    @Override
    public String getMod() {
        return "minecraft";
    }

    @Override
    public boolean areItemsEqual(ItemStackJS other) {
        return other.isEmpty();
    }

    @Override
    public boolean areItemsEqual(ItemStack other) {
        return other.isEmpty();
    }

    @Override
    public boolean isNBTEqual(ItemStackJS other) {
        return !other.hasNBT();
    }

    @Override
    public boolean isNBTEqual(ItemStack other) {
        return !other.hasTag();
    }

    @Override
    public boolean equals(Object o) {
        return of(o).isEmpty();
    }

    @Override
    public boolean strongEquals(Object o) {
        return of(o).isEmpty();
    }

    @Override
    public int getHarvestLevel(ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
        return -1;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "minecraft:air");
        return json;
    }

    @Override
    public JsonElement toRawResultJson() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "minecraft:air");
        json.addProperty("count", 1);
        return json;
    }

    @Override
    public void onChanged(@Nullable Tag o) {
    }

    @Override
    public String getItemGroup() {
        return "";
    }
}
