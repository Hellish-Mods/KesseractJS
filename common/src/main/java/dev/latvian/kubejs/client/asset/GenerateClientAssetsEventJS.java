package dev.latvian.kubejs.client.asset;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.client.ModelGenerator;
import dev.latvian.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class GenerateClientAssetsEventJS extends EventJS {
	public final AssetJsonGenerator generator;

	public GenerateClientAssetsEventJS(AssetJsonGenerator gen) {
		this.generator = gen;
	}

	public void add(ResourceLocation location, JsonElement json) {
		generator.json(location, json);
	}

	public void addModel(String type, ResourceLocation id, Consumer<ModelGenerator> consumer) {
		var gen = Util.make(new ModelGenerator(), consumer);
        add(new ResourceLocation(id.getNamespace(), String.format("models/%s/%s", type, id.getPath())), gen.toJson());
    }

	public void addBlockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
		var gen = Util.make(new VariantBlockStateGenerator(), consumer);
		add(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void addMultipartBlockState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
		var gen = Util.make(new MultipartBlockStateGenerator(), consumer);
		add(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void defaultItemModel(ResourceLocation id) {
		addModel("item", id, model -> {
			model.parent("minecraft:item/generated");
			model.texture("layer0", id.getNamespace() + ":item/" + id.getPath());
		});
	}

	public void defaultHandheldItemModel(ResourceLocation id) {
		addModel("item", id, model -> {
			model.parent("minecraft:item/handheld");
			model.texture("layer0", id.getNamespace() + ":item/" + id.getPath());
		});
	}
}