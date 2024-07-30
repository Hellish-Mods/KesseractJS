package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJSEvents;
import me.shedaniel.architectury.event.events.BlockEvent;
import me.shedaniel.architectury.event.events.InteractionEvent;
import me.shedaniel.architectury.utils.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSBlockEventHandler {

	public static void init() {
		if (!CommonProperties.get().serverOnly) {
			registry();
		}

		InteractionEvent.RIGHT_CLICK_BLOCK.register(KubeJSBlockEventHandler::rightClick);
		InteractionEvent.LEFT_CLICK_BLOCK.register(KubeJSBlockEventHandler::leftClick);
		BlockEvent.BREAK.register(KubeJSBlockEventHandler::blockBreak);
		BlockEvent.PLACE.register(KubeJSBlockEventHandler::blockPlace);
        BlockEvent.FALLING_LAND.register(KubeJSBlockEventHandler::blockLanding);
	}

    private static void blockLanding(
        Level level,
        BlockPos pos,
        BlockState fallState,
        BlockState landOn,
        FallingBlockEntity entity
    ) {
        new BlockLandingEventJS(level, pos, fallState, landOn, entity).post(KubeJSEvents.BLOCK_LANDING);
    }

    private static void registry() {
	}

	private static InteractionResult rightClick(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		if (player != null
            && player.level != null
            && !player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem())
            && new BlockRightClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_RIGHT_CLICK)
        ) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private static InteractionResult leftClick(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		if (player != null && player.level != null && new BlockLeftClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_LEFT_CLICK)) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private static InteractionResult blockBreak(Level world, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) {
		if (player != null && player.level != null && new BlockBreakEventJS(player, world, pos, state, xp).post(KubeJSEvents.BLOCK_BREAK)) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private static InteractionResult blockPlace(Level world, BlockPos pos, BlockState state, @Nullable Entity placer) {
		if (world != null && (placer == null || placer.level != null) && new BlockPlaceEventJS(placer, world, pos, state).post(KubeJSEvents.BLOCK_PLACE)) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	/*
	private static void blockDrops(BlockEvent.HarvestDropsEvent event)
	{
		if (event.getWorld().isRemote())
		{
			return;
		}

		BlockDropsEventJS e = new BlockDropsEventJS(event);
		e.post(KubeJSEvents.BLOCK_DROPS);

		if (e.dropList != null)
		{
			event.getDrops().clear();

			for (ItemStackJS stack : e.dropList)
			{
				event.getDrops().add(stack.getItemStack());
			}
		}
	}
	 */
}