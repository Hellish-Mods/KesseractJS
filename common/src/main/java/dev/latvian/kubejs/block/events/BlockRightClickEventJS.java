package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
@RequiredArgsConstructor
public class BlockRightClickEventJS extends PlayerEventJS {
	private final Player player;
	@Getter
    private final InteractionHand hand;
	private final BlockPos pos;
	private final Direction direction;
	private BlockContainerJS block;
	private ItemStackJS item;

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public BlockContainerJS getBlock() {
        return block == null ? (block = new BlockContainerJS(player.level, pos)) : block;
	}

    public ItemStackJS getItem() {
		if (item == null) {
			item = ItemStackJS.of(player.getItemInHand(hand));
		}

		return item;
	}

	public Direction getFacing() {
		return direction;
	}
}