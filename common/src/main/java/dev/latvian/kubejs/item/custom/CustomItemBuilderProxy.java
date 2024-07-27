package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * @author ZZZank
 */
public class CustomItemBuilderProxy {
    public static class Sword extends ItemBuilder {
        public Sword(ResourceLocation i) {
            super(i);
            type(ToolItemType.SWORD);
        }
    }
    public static class Pickaxe extends ItemBuilder {
        public Pickaxe(ResourceLocation i) {
            super(i);
            type(ToolItemType.PICKAXE);
        }
    }
    public static class Axe extends ItemBuilder {
        public Axe(ResourceLocation i) {
            super(i);
            type(ToolItemType.AXE);
        }
    }
    public static class Shovel extends ItemBuilder {
        public Shovel(ResourceLocation i) {
            super(i);
            type(ToolItemType.SHOVEL);
        }
    }
//    public static class Shears extends ItemBuilder {
//        public Shears(ResourceLocation i) {
//            super(i);
//            type(ToolItemType.);
//        }
//    }
    public static class Hoe extends ItemBuilder {
        public Hoe(ResourceLocation i) {
            super(i);
            type(ToolItemType.HOE);
        }
    }
    public static class Helmet extends ItemBuilder {
        public Helmet(ResourceLocation i) {
            super(i);
            type(ArmorItemType.HELMET);
        }
    }
    public static class Chestplate extends ItemBuilder {
        public Chestplate(ResourceLocation i) {
            super(i);
            type(ArmorItemType.CHESTPLATE);
        }
    }
    public static class Leggings extends ItemBuilder {
        public Leggings(ResourceLocation i) {
            super(i);
            type(ArmorItemType.LEGGINGS);
        }
    }
    public static class Boots extends ItemBuilder {
        public Boots(ResourceLocation i) {
            super(i);
            type(ArmorItemType.BOOTS);
        }
    }
}
