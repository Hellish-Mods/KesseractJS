package dev.latvian.kubejs.block.custom.builder;

import dev.latvian.kubejs.block.custom.StoneButtonBlockJS;
import dev.latvian.kubejs.block.custom.WoodenButtonBlockJS;
import dev.latvian.kubejs.client.ModelGenerator;
import dev.latvian.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class ButtonBlockBuilder extends ShapedBlockBuilder {
    public boolean sensitive;

	public ButtonBlockBuilder(ResourceLocation i) {
		super(i, "_button");
		noCollission();
		tagBoth(BlockTags.BUTTONS.getName());
	}

    public ButtonBlockBuilder wooden() {
        sensitive = true;
        return this;
    }

    public ButtonBlockBuilder stone() {
        sensitive = false;
        return this;
    }

    @Override
    public Block createObject() {
        return sensitive ? new WoodenButtonBlockJS(createProperties()) : new StoneButtonBlockJS(createProperties());
    }

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		val mod0 = newID("block/", "").toString();
		val mod1 = newID("block/", "_pressed").toString();

		bs.variant("face=ceiling,facing=east,powered=false", v -> v.model(mod0).x(180).y(270));
		bs.variant("face=ceiling,facing=east,powered=true", v -> v.model(mod1).x(180).y(270));
		bs.variant("face=ceiling,facing=north,powered=false", v -> v.model(mod0).x(180).y(180));
		bs.variant("face=ceiling,facing=north,powered=true", v -> v.model(mod1).x(180).y(180));
		bs.variant("face=ceiling,facing=south,powered=false", v -> v.model(mod0).x(180));
		bs.variant("face=ceiling,facing=south,powered=true", v -> v.model(mod1).x(180));
		bs.variant("face=ceiling,facing=west,powered=false", v -> v.model(mod0).x(180).y(90));
		bs.variant("face=ceiling,facing=west,powered=true", v -> v.model(mod1).x(180).y(90));
		bs.variant("face=floor,facing=east,powered=false", v -> v.model(mod0).y(90));
		bs.variant("face=floor,facing=east,powered=true", v -> v.model(mod1).y(90));
		bs.variant("face=floor,facing=north,powered=false", v -> v.model(mod0));
		bs.variant("face=floor,facing=north,powered=true", v -> v.model(mod1));
		bs.variant("face=floor,facing=south,powered=false", v -> v.model(mod0).y(180));
		bs.variant("face=floor,facing=south,powered=true", v -> v.model(mod1).y(180));
		bs.variant("face=floor,facing=west,powered=false", v -> v.model(mod0).y(270));
		bs.variant("face=floor,facing=west,powered=true", v -> v.model(mod1).y(270));
		bs.variant("face=wall,facing=east,powered=false", v -> v.model(mod0).x(90).y(90).uvlock());
		bs.variant("face=wall,facing=east,powered=true", v -> v.model(mod1).x(90).y(90).uvlock());
		bs.variant("face=wall,facing=north,powered=false", v -> v.model(mod0).x(90).uvlock());
		bs.variant("face=wall,facing=north,powered=true", v -> v.model(mod1).x(90).uvlock());
		bs.variant("face=wall,facing=south,powered=false", v -> v.model(mod0).x(90).y(180).uvlock());
		bs.variant("face=wall,facing=south,powered=true", v -> v.model(mod1).x(90).y(180).uvlock());
		bs.variant("face=wall,facing=west,powered=false", v -> v.model(mod0).x(90).y(270).uvlock());
		bs.variant("face=wall,facing=west,powered=true", v -> v.model(mod1).x(90).y(270).uvlock());
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		val texture = textures.get("texture").getAsString();

		generator.blockModel(id, m -> {
			m.parent("minecraft:block/button");
			m.texture("texture", texture);
		});

		generator.blockModel(newID("", "_pressed"), m -> {
			m.parent("minecraft:block/button_pressed");
			m.texture("texture", texture);
		});
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent("minecraft:block/button_inventory");
		m.texture("texture", textures.get("texture").getAsString());
	}

    public static class Stone extends ButtonBlockBuilder {
        public Stone(ResourceLocation i) {
            super(i);
            stone();
        }
    }

    public static class Wooden extends ButtonBlockBuilder {
        public Wooden(ResourceLocation i) {
            super(i);
            wooden();
            tagBoth(BlockTags.WOODEN_BUTTONS.getName());
        }
    }
}