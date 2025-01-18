package dev.latvian.kubejs.generator;

import dev.latvian.kubejs.client.ModelGenerator;
import dev.latvian.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.kubejs.script.data.GeneratedData;
import dev.latvian.kubejs.util.ConsoleJS;
import lombok.val;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

public class AssetJsonGenerator extends JsonGenerator {
	public AssetJsonGenerator(Map<ResourceLocation, GeneratedData> m) {
		super(ConsoleJS.CLIENT, m);
	}

	public void blockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
		val gen = Util.make(new VariantBlockStateGenerator(), consumer);
		json(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void multipartState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
		val gen = Util.make(new MultipartBlockStateGenerator(), consumer);
		json(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void blockModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
		val gen = Util.make(new ModelGenerator(), consumer);
		json(new ResourceLocation(id.getNamespace(), "models/block/" + id.getPath()), gen.toJson());
	}

	public void itemModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
		val gen = Util.make(new ModelGenerator(), consumer);
		json(asItemModelLocation(id), gen.toJson());
	}

	public static ResourceLocation asItemModelLocation(ResourceLocation id) {
		return new ResourceLocation(id.getNamespace(), "models/item/" + id.getPath());
	}
}
