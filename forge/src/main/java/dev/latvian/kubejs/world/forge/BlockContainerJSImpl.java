package dev.latvian.kubejs.world.forge;

import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemHandler;
import lombok.AllArgsConstructor;
import lombok.val;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class BlockContainerJSImpl {
	public static InventoryJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing) {
		val handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).orElse(null);

		if (handler != null) {
			if (handler instanceof IItemHandlerModifiable modifiable) {
				return new InventoryJS(new ItemHandlerProxy.Mutable(modifiable));
			}
			return new InventoryJS(new ItemHandlerProxy(handler));
		}

		return null;
	}

    @AllArgsConstructor
    static class ItemHandlerProxy implements ItemHandler {
        protected final IItemHandler handler;

        @Override
        public int getSlots() {
            return handler.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            return handler.getStackInSlot(i);
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean b) {
            return handler.insertItem(i, itemStack, b);
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int i1, boolean b) {
            return handler.extractItem(i, i1, b);
        }

        @Override
        public int getSlotLimit(int i) {
            return handler.getSlotLimit(i);
        }

        @Override
        public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
            return handler.isItemValid(i, itemStack);
        }

        static class Mutable extends ItemHandlerProxy implements ItemHandler.Mutable {

            public Mutable(IItemHandlerModifiable handler) {
                super(handler);
            }

            @Override
            public void setStackInSlot(int i, @Nonnull ItemStack itemStack) {
                ((IItemHandlerModifiable) handler).setStackInSlot(i, itemStack);
            }
        }
    }
}
