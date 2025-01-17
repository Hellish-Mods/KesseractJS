package dev.latvian.kubejs.block.custom.builder;

import dev.latvian.kubejs.client.ModelGenerator;
import dev.latvian.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;

public class PressurePlateBlockBuilder extends ShapedBlockBuilder {
	public transient PressurePlateBlock.Sensitivity sensitivity;

	public PressurePlateBlockBuilder(ResourceLocation i) {
		super(i, "_pressure_plate");
		noCollission();
		tagBoth(BlockTags.PRESSURE_PLATES.getName());
		sensitivity = PressurePlateBlock.Sensitivity.EVERYTHING;
	}

	public PressurePlateBlockBuilder sensitivity(PressurePlateBlock.Sensitivity sensitivity) {
		this.sensitivity = sensitivity;
		return this;
	}

	@Override
	public Block createObject() {
        return new PressurePlateBlockJS(sensitivity, createProperties());
    }

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		bs.variant("powered=true", v -> v.model(newID("block/", "_down").toString()));
		bs.variant("powered=false", v -> v.model(newID("block/", "_up").toString()));
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		val texture = textures.get("texture").getAsString();

		generator.blockModel(newID("", "_down"), m -> {
			m.parent("minecraft:block/pressure_plate_down");
			m.texture("texture", texture);
		});

		generator.blockModel(newID("", "_up"), m -> {
			m.parent("minecraft:block/pressure_plate_up");
			m.texture("texture", texture);
		});
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent(newID("block/", "_up").toString());
	}

    public static class PressurePlateBlockJS extends PressurePlateBlock {

        public PressurePlateBlockJS(Sensitivity sensitivity, Properties properties) {
            super(sensitivity, properties);
        }
    }

    public static class Wooden extends PressurePlateBlockBuilder {
        public Wooden(ResourceLocation i) {
            super(i);
            sensitivity(PressurePlateBlock.Sensitivity.EVERYTHING);
            tagBoth(BlockTags.WOODEN_PRESSURE_PLATES.getName());
        }
    }

    public static class Stone extends PressurePlateBlockBuilder {
        public Stone(ResourceLocation i) {
            super(i);
            sensitivity(PressurePlateBlock.Sensitivity.MOBS);
            tagBoth(BlockTags.STONE_PRESSURE_PLATES.getName());
        }
    }
}