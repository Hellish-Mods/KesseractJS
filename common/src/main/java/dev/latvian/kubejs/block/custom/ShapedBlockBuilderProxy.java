package dev.latvian.kubejs.block.custom;

import dev.latvian.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * @author ZZZank
 */
public class ShapedBlockBuilderProxy {
    public static class Fence extends BlockBuilder {
        public Fence(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.FENCE);
        }
    }
    public static class FenceGate extends BlockBuilder {
        public FenceGate(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.FENCE_GATE);
        }
    }
    public static class Slab extends BlockBuilder {
        public Slab(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.SLAB);
        }
    }
    public static class Stairs extends BlockBuilder {
        public Stairs(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.STAIRS);
        }
    }
    public static class StoneButton extends BlockBuilder {
        public StoneButton(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.STONE_BUTTON);
        }
    }
    public static class StonePressurePlate extends BlockBuilder {
        public StonePressurePlate(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.STONE_PRESSURE_PLATE);
        }
    }
    public static class Wall extends BlockBuilder {
        public Wall(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.WALL);
        }
    }
    public static class WoodenButton extends BlockBuilder {
        public WoodenButton(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.WOODEN_BUTTON);
        }
    }
    public static class WoodenPressurePlate extends BlockBuilder {
        public WoodenPressurePlate(ResourceLocation id) {
            super(id);
            this.type(ShapedBlockType.WOODEN_PRESSURE_PLATE);
        }
    }
}
