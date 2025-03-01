package dev.latvian.kubejs.fluid;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.bindings.RarityWrapper;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.mods.rhino.annotations.typing.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;

/**
 * @author LatvianModder
 */
@ReturnsSelf
public class FluidBuilder extends BuilderBase<Fluid> {
	public String stillTexture;
	public String flowingTexture;
	public int color = 0xFFFFFFFF;
	public int bucketColor = 0xFFFFFFFF;
	public int luminosity = 0;
	public int density = 1000;
	public int temperature = 300;
	public int viscosity = 1000;
	public boolean isGaseous;
	public RarityWrapper rarity = RarityWrapper.COMMON;
    public transient String renderType = "solid";
	public Object extraPlatformInfo;

	public FlowingFluid stillFluid;
	public FlowingFluid flowingFluid;
	public BucketItem bucketItem;
	public LiquidBlock block;

	private JsonObject blockstateJson;
	private JsonObject blockModelJson;

    public FluidBuilder(ResourceLocation id) {
        super(id);
        textureStill( KubeJS.id("fluid/fluid_thin"));
        textureFlowing(KubeJS.id("fluid/fluid_thin_flow"));
    }

    @Override
	public RegistryInfo<Fluid> getRegistryType() {
		return RegistryInfos.FLUID;
	}

	@Override
	public Fluid createObject() {
        stillFluid = KubeJSFluidEventHandler.buildFluid(true, this);
        return stillFluid;
	}

    @Override
    public void createAdditionalObjects() {
        block = KubeJSFluidEventHandler.buildFluidBlock(
            this,
            Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()
        );
        flowingFluid = KubeJSFluidEventHandler.buildFluid(false, this);
        bucketItem = KubeJSFluidEventHandler.buildBucket(this);
        KubeJSRegistries.fluids().register(newID("flowing_", ""), () -> flowingFluid);
        KubeJSRegistries.blocks().register(id, () -> block);
        KubeJSRegistries.items().register(newID("", "_bucket"), () -> bucketItem);
    }

    public FluidBuilder color(int c) {
		color = c;

		if ((color & 0xFFFFFF) == color) {
			color |= 0xFF000000;
		}

		return bucketColor(color);
	}

	public FluidBuilder bucketColor(int c) {
		bucketColor = c;

		if ((bucketColor & 0xFFFFFF) == bucketColor) {
			bucketColor |= 0xFF000000;
		}

		return this;
	}

	public FluidBuilder textureStill(ResourceLocation id) {
		stillTexture = id.toString();
		return this;
	}
	public FluidBuilder stillTexture(ResourceLocation id) {
		return textureStill(id);
	}

	public FluidBuilder textureFlowing(ResourceLocation id) {
		flowingTexture = id.toString();
		return this;
	}
	public FluidBuilder flowingTexture(ResourceLocation id) {
		return textureFlowing(id);
	}

    public FluidBuilder textureThick(int color) {
        return textureStill(KubeJS.id("fluid/fluid_thick"))
            .textureFlowing(KubeJS.id("fluid/fluid_thick_flow"))
            .color(color);
    }
	public FluidBuilder thickTexture(int color) {
		return textureThick(color);
	}

	public FluidBuilder textureThin(int color) {
		return textureStill(KubeJS.id("fluid/fluid_thin"))
				.textureFlowing(KubeJS.id("fluid/fluid_thin_flow"))
				.color(color);
	}
	public FluidBuilder thinTexture(int color) {
		return textureThin(color);
	}

	public FluidBuilder luminosity(int luminosity) {
		this.luminosity = luminosity;
		return this;
	}

	public FluidBuilder density(int density) {
		this.density = density;
		return this;
	}

	public FluidBuilder temperature(int temperature) {
		this.temperature = temperature;
		return this;
	}

	public FluidBuilder viscosity(int viscosity) {
		this.viscosity = viscosity;
		return this;
	}

	public FluidBuilder gaseous() {
		isGaseous = true;
		return this;
	}

	public FluidBuilder rarity(RarityWrapper rarity) {
		this.rarity = rarity;
		return this;
	}

    public FluidBuilder renderType(String l) {
        renderType = l;
        return this;
    }

    public FluidBuilder translucent() {
        return renderType("translucent");
    }

	public void setBlockstateJson(JsonObject o) {
		blockstateJson = o;
	}

	public JsonObject getBlockstateJson() {
		if (blockstateJson == null) {
			blockstateJson = new JsonObject();
			JsonObject variants = new JsonObject();
			JsonObject modelo = new JsonObject();
			modelo.addProperty("model", newID("block/", "").toString());
			variants.add("", modelo);
			blockstateJson.add("variants", variants);
		}

		return blockstateJson;
	}

	public void setBlockModelJson(JsonObject o) {
		blockModelJson = o;
	}

	public JsonObject getBlockModelJson() {
		if (blockModelJson == null) {
			blockModelJson = new JsonObject();
			JsonObject textures = new JsonObject();
			textures.addProperty("particle", stillTexture);
			blockModelJson.add("textures", textures);
			return blockModelJson;
		}

		return blockModelJson;
	}

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
        generator.json(this.newID("blockstates/", ""), this.getBlockstateJson());
        generator.json(this.newID("models/block/", ""), this.getBlockModelJson());

        JsonObject bucketModel = new JsonObject();
        bucketModel.addProperty("parent", "kubejs:item/generated_bucket");
        generator.json(this.newID("models/item/", "_bucket"), bucketModel);
    }

    public FluidBuilder noBucket() {
        bucketItem = null;
        return this;
    }

    public FluidBuilder noBlock() {
        block = null;
        return this;
    }
}