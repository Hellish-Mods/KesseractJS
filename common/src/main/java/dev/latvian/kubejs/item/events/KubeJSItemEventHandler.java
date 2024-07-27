package dev.latvian.kubejs.item.events;

import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.core.ItemKJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.BlockItemJS;
import dev.latvian.kubejs.player.InventoryChangedEventJS;
import dev.latvian.kubejs.registry.RegistryInfos;
import me.shedaniel.architectury.event.events.InteractionEvent;
import me.shedaniel.architectury.event.events.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class KubeJSItemEventHandler {
	public static Supplier<Item> DUMMY_FLUID_ITEM = () -> Items.STRUCTURE_VOID;

	public static void init() {
		if (!CommonProperties.get().serverOnly) {
			registry();
		}

		InteractionEvent.RIGHT_CLICK_ITEM.register(KubeJSItemEventHandler::rightClick);
		InteractionEvent.CLIENT_RIGHT_CLICK_AIR.register(KubeJSItemEventHandler::rightClickEmpty);
		InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(KubeJSItemEventHandler::leftClickEmpty);
		PlayerEvent.PICKUP_ITEM_PRE.register(KubeJSItemEventHandler::pickup);
		PlayerEvent.DROP_ITEM.register(KubeJSItemEventHandler::drop);
		InteractionEvent.INTERACT_ENTITY.register(KubeJSItemEventHandler::entityInteract);
		PlayerEvent.CRAFT_ITEM.register(KubeJSItemEventHandler::crafted);
		PlayerEvent.SMELT_ITEM.register(KubeJSItemEventHandler::smelted);
	}

    private static void registry() {
        DUMMY_FLUID_ITEM = KubeJSRegistries.items().register(
            KubeJS.id("dummy_fluid_item"),
            () -> new Item(new Item.Properties().stacksTo(1).tab(KubeJS.tab))
        );
    }

	private static InteractionResultHolder<ItemStack> rightClick(Player player, InteractionHand hand) {
		if (!player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem()) && new ItemRightClickEventJS(player, hand).post(KubeJSEvents.ITEM_RIGHT_CLICK)) {
			return InteractionResultHolder.success(player.getItemInHand(hand));
		}

		return InteractionResultHolder.pass(ItemStack.EMPTY);
	}

	private static void rightClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null) {
			new ItemRightClickEmptyEventJS(player, hand).post(KubeJSEvents.ITEM_RIGHT_CLICK_EMPTY);
		}
	}

	private static void leftClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null) {
			new ItemLeftClickEventJS(player, hand).post(KubeJSEvents.ITEM_LEFT_CLICK);
		}
	}

	private static InteractionResult pickup(Player player, ItemEntity entity, ItemStack stack) {
		if (player != null && entity != null && player.level != null && new ItemPickupEventJS(player, entity, stack).post(KubeJSEvents.ITEM_PICKUP)) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private static InteractionResult drop(Player player, ItemEntity entity) {
		if (player != null && entity != null && player.level != null && new ItemTossEventJS(player, entity).post(KubeJSEvents.ITEM_TOSS)) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private static InteractionResult entityInteract(Player player, Entity entity, InteractionHand hand) {
        if (player == null || entity == null || player.level == null) {
            return InteractionResult.PASS;
        }
        boolean cancelled = new ItemEntityInteractEventJS(player, entity, hand).post(KubeJSEvents.ITEM_ENTITY_INTERACT);
        return cancelled ? InteractionResult.FAIL : InteractionResult.PASS;
    }

	private static void crafted(Player player, ItemStack crafted, Container grid) {
		if (player instanceof ServerPlayer && !crafted.isEmpty()) {
			new ItemCraftedEventJS(player, crafted, grid).post(KubeJSEvents.ITEM_CRAFTED);
			new InventoryChangedEventJS((ServerPlayer) player, crafted, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	private static void smelted(Player player, ItemStack smelted) {
		if (player instanceof ServerPlayer && !smelted.isEmpty()) {
			new ItemSmeltedEventJS(player, smelted).post(KubeJSEvents.ITEM_SMELTED);
			new InventoryChangedEventJS((ServerPlayer) player, smelted, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}
}