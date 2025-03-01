package dev.latvian.kubejs.block.custom.builder;

import dev.latvian.kubejs.client.ModelGenerator;
import dev.latvian.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.rhino.annotations.typing.ReturnsSelf;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;

@ReturnsSelf
public class FenceBlockBuilder extends MultipartShapedBlockBuilder {
	public FenceBlockBuilder(ResourceLocation i) {
		super(i, "_fence");

		tagBoth(BlockTags.FENCES.getName());

		if (Platform.isForge()) {
			tagBoth(new ResourceLocation("forge:fences"));
		}
	}

	@Override
	public Block createObject() {
		return new FenceBlock(createProperties());
	}

	@Override
	protected void generateMultipartBlockStateJson(MultipartBlockStateGenerator bs) {
		val modPost = newID("block/", "_post").toString();
		val modSide = newID("block/", "_side").toString();

		bs.part("", modPost);
		bs.part("north=true", p -> p.model(modSide).uvlock());
		bs.part("east=true", p -> p.model(modSide).uvlock().y(90));
		bs.part("south=true", p -> p.model(modSide).uvlock().y(180));
		bs.part("west=true", p -> p.model(modSide).uvlock().y(270));
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent("minecraft:block/fence_inventory");
		m.texture("texture", textures.get("texture").getAsString());
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		val texture = textures.get("texture").getAsString();

		generator.blockModel(newID("", "_post"), m -> {
			m.parent("minecraft:block/fence_post");
			m.texture("texture", texture);
		});
		generator.blockModel(newID("", "_side"), m -> {
			m.parent("minecraft:block/fence_side");
			m.texture("texture", texture);
		});
	}
}
