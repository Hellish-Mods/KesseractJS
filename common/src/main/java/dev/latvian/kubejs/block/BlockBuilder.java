package dev.latvian.kubejs.block;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.custom.BasicBlockJS;
import dev.latvian.kubejs.block.custom.BlockType;
import dev.latvian.kubejs.client.ModelGenerator;
import dev.latvian.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.kubejs.core.BlockKJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.generator.DataJsonGenerator;
import dev.latvian.kubejs.loot.LootBuilder;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.val;
import me.shedaniel.architectury.registry.BlockPropertiesExtension;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class BlockBuilder extends BuilderBase<Block> {

    @Deprecated
    public transient BlockType type = null;
    public transient MaterialJS material = MaterialListJS.INSTANCE.map.get("wood");
    public transient SoundType soundType = material.getSound();
    public transient Function<BlockState, MaterialColor> materialColorFn
        = b -> material.getMinecraftMaterial().getColor();
    public transient float hardness = 0.5F;
    public transient float resistance = -1F;
    public transient float lightLevel = 0F;
    public transient ToolType harvestTool = null;
    public transient int harvestLevel = -1;
    public transient boolean opaque = true;
    public transient boolean fullBlock = false;
    public transient boolean requiresTool = false;
    public transient String renderType = "solid";
    public transient Int2IntOpenHashMap color = new Int2IntOpenHashMap();
    public transient BlockTintFunction tint;
    public transient final JsonObject textures = new JsonObject();
    public transient String model = "";
    public transient BlockItemBuilder itemBuilder;
    public transient List<AABB> customShape = new ArrayList<>();
    public transient boolean noCollission = false;
    public transient boolean notSolid = false;
    public transient float slipperiness = 0.6F;
    public transient float speedFactor = 1.0F;
    public transient float jumpFactor = 1.0F;
    public Consumer<RandomTickCallbackJS> randomTickCallback = null;
    public Consumer<LootBuilder> lootTable;
    public JsonObject blockstateJson = null;
    public JsonObject modelJson = null;
    public transient boolean noValidSpawns = false;
    public transient boolean suffocating = true;
    public transient boolean viewBlocking = true;
    public transient boolean redstoneConductor = true;
    public transient boolean transparent = false;
    public transient Set<Property<?>> blockStateProperties = new HashSet<>();

    public BlockBuilder(ResourceLocation id) {
        super(id);
        color.defaultReturnValue(0xFFFFFFFF);
        textureAll(id.getNamespace() + ":block/" + id.getPath());
        itemBuilder = new BlockItemBuilder(id);
        itemBuilder.blockBuilder = this;

        lootTable = loot -> loot.addPool(pool -> {
            pool.survivesExplosion();
            pool.addItem(new ItemStack(get()));
        });
    }

    @Override
    public RegistryInfo<Block> getRegistryType() {
        return RegistryInfos.BLOCK;
    }

    @Override
    public Block createObject() {
        return type == null
            ? new BasicBlockJS(this)
            : this.type.createBlock(this);
    }

    @Override
    public Block transformObject(Block obj) {
        ((BlockKJS) obj).setBlockBuilderKJS(this);
        return obj;
    }

    @Override
    public void createAdditionalObjects() {
        if (this.itemBuilder != null) {
            KubeJSRegistries.items().register(itemBuilder.id, () -> itemBuilder.get());
        }
    }

    @Override
    public String getBuilderType() {
        return "block";
    }

    @Deprecated
    public Set<String> getDefaultTags() {
        return tags.stream().map(ResourceLocation::toString).collect(Collectors.toSet());
    }

    public BlockBuilder type(BlockType t) {
        type = t;
        type.applyDefaults(this);
        return this;
    }

    @JSInfo("Sets the block's map color. Defaults to NONE.")
    public BlockBuilder materialColor(MaterialColor m) {
        materialColorFn = MapColorHelper.reverse(m);
        return this;
    }

    @JSInfo("Sets the block's map color dynamically per block state. If unset, defaults to NONE.")
    public BlockBuilder dynamicMaterialColor(@Nullable Function<BlockState, Object> m) {
        materialColorFn = m == null ? MapColorHelper.NONE : s -> MapColorHelper.of(m.apply(s));
        return this;
    }

    public BlockBuilder material(MaterialJS m) {
        material = m;
        return this;
    }

    @JSInfo("Sets the block's sound type. Defaults to what matches the material of this block.")
    public BlockBuilder soundType(SoundType m) {
        if (m == null) {
            soundType = null;
            ConsoleJS.STARTUP.error("Invalid sound type!");
            ConsoleJS.STARTUP.warn("Valid sound types: " + SoundTypeWrapper.INSTANCE.getMap().keySet());
            return this;
        }

        soundType = m;
        return this;
    }

    @JSInfo("Set the sound type to what matches the material of this block")
    public BlockBuilder noSoundType() {
        return soundType(null);
    }

    public BlockBuilder woodSoundType() {
        return soundType(SoundType.WOOD);
    }

    public BlockBuilder stoneSoundType() {
        return soundType(SoundType.STONE);
    }

    public BlockBuilder gravelSoundType() {
        return soundType(SoundType.GRAVEL);
    }

    public BlockBuilder grassSoundType() {
        return soundType(SoundType.GRASS);
    }

    public BlockBuilder sandSoundType() {
        return soundType(SoundType.SAND);
    }

    public BlockBuilder cropSoundType() {
        return soundType(SoundType.CROP);
    }

    public BlockBuilder glassSoundType() {
        return soundType(SoundType.GLASS);
    }

    @JSInfo("""
        Sets the hardness of the block. Defaults to 0.5.
        
        Setting this to -1 will make the block unbreakable like bedrock.""")
    public BlockBuilder hardness(float h) {
        hardness = h;
        return this;
    }

    @JSInfo("""
        Sets the blast resistance of the block. Defaults to -1.""")
    public BlockBuilder resistance(float r) {
        resistance = r;
        return this;
    }

    @JSInfo("Makes the block unbreakable.")
    public BlockBuilder unbreakable() {
        hardness = -1F;
        resistance = Float.MAX_VALUE;
        return this;
    }

    @JSInfo("Sets the light level of the block. Defaults to 0 (no light). providing a `1` equals to 15 light level for built block")
    public BlockBuilder lightLevel(float light) {
        lightLevel = light;
        return this;
    }

    public BlockBuilder harvestTool(ToolType tool, int level) {
        harvestTool = tool;
        harvestLevel = level;
        return this;
    }

    @JSInfo("Sets the opacity of the block. Opaque blocks do not let light through.")
    public BlockBuilder opaque(boolean o) {
        opaque = o;
        return this;
    }

    @JSInfo("Sets the block should be a full block or not, like cactus or doors.")
    public BlockBuilder fullBlock(boolean f) {
        fullBlock = f;
        return this;
    }

    @JSInfo("Makes the block require a tool to have drops when broken.")
    public BlockBuilder requiresTool(boolean f) {
        requiresTool = f;
        return this;
    }


    @JSInfo("""
        Sets the render type of the block. Can be `cutout`, `cutout_mipped`, `translucent`, or `basic`.""")
    public BlockBuilder renderType(String l) {
        renderType = l;
        return this;
    }

    @Override
    public void generateDataJsons(DataJsonGenerator generator) {
        if (this.lootTable == null) {
            return;
        }

        val lootBuilder = new LootBuilder(null);
        lootBuilder.type = "minecraft:block";

        if (lootTable != null) {
            lootTable.accept(lootBuilder);
        } else if (get().asItem() != Items.AIR) {
            lootBuilder.addPool(pool -> {
                pool.survivesExplosion();
                pool.addItem(new ItemStack(get()));
            });
        }

        val json = lootBuilder.toJson();
        generator.json(newID("loot_tables/blocks/", ""), json);
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
        if (type != null) {
            type.generateAssets(this, generator);
            return;
        }
        if (blockstateJson != null) {
            generator.json(newID("blockstates/", ""), blockstateJson);
        } else {
            generator.blockState(id, this::generateBlockStateJson);
        }

        if (modelJson != null) {
            generator.json(newID("models/block/", ""), modelJson);
        } else {
            // This is different because there can be multiple models, so we should let the block handle those
            generateBlockModelJsons(generator);
        }

        if (itemBuilder != null) {
            if (itemBuilder.modelJson != null) {
                generator.json(newID("models/item/", ""), itemBuilder.modelJson);
            } else {
                generator.itemModel(itemBuilder.id, this::generateItemModelJson);
            }
        }

    }

    protected void generateItemModelJson(ModelGenerator m) {
        if (!model.isEmpty()) {
            m.parent(model);
        } else {
            m.parent(newID("block/", "").toString());
        }
    }

    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        if (type != null) {
            type.generateBlockModels(this).forEach(generator::json);
            return;
        }
        generator.blockModel(id, mg -> {
            val particle = textures.get("particle").getAsString();

            if (areAllTexturesEqual(textures, particle)) {
                mg.parent("minecraft:block/cube_all");
                mg.texture("all", particle);
            } else {
                mg.parent("block/cube");
                mg.textures(textures);
            }

            if (tint != null || !customShape.isEmpty()) {
                List<AABB> boxes = new ArrayList<>(customShape);

                if (boxes.isEmpty()) {
                    boxes.add(new AABB(0D, 0D, 0D, 1D, 1D, 1D));
                }

                for (val box : boxes) {
                    mg.element(e -> {
                        e.box(box);

                        for (val direction : Direction.values()) {
                            e.face(direction, face -> {
                                face.tex("#" + direction.getSerializedName());
                                face.cull();

                                if (tint != null) {
                                    face.tintindex(0);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        bs.variant("", model.isEmpty() ? (id.getNamespace() + ":block/" + id.getPath()) : model);
    }

    protected boolean areAllTexturesEqual(JsonObject tex, String t) {
        for (val direction : Direction.values()) {
            if (!tex.get(direction.getSerializedName()).getAsString().equals(t)) {
                return false;
            }
        }

        return true;
    }

    @JSInfo("""
        Set the color of a specific layer of the block.""")
    public BlockBuilder color(int index, BlockTintFunction color) {
        if (!(tint instanceof BlockTintFunction.Mapped)) {
            tint = new BlockTintFunction.Mapped();
        }

        ((BlockTintFunction.Mapped) tint).map.put(index, color);
        return this;
    }

    @JSInfo("""
        Set the color of a specific layer of the block.""")
    public BlockBuilder color(BlockTintFunction color) {
        tint = color;
        return this;
    }

    @JSInfo("use 'textureAll(tex)' instead")
    @Deprecated
    public BlockBuilder texture(String tex) {
        ScriptType.STARTUP.console.warn(
            "Using 'texture(tex)' in block builders is deprecated! Please use 'textureAll(tex)' instead!");
        return textureAll(tex);
    }

    @JSInfo("""
        Texture the block on all sides with the same texture.""")
    public BlockBuilder textureAll(String tex) {
        for (Direction direction : Direction.values()) {
            textureSide(direction, tex);
        }

        textures.addProperty("particle", tex);
        return this;
    }

    @JSInfo("""
        Texture a specific side of the block.""")
    public BlockBuilder textureSide(Direction direction, String tex) {
        return texture(direction.getSerializedName(), tex);
    }

    @JSInfo("""
        Texture a specific texture key of the block.""")
    public BlockBuilder texture(String id, String tex) {
        textures.addProperty(id, tex);
        return this;
    }

    @JSInfo("""
        Set the block's model.""")
    public BlockBuilder model(String m) {
        model = m;
        itemBuilder.parentModel = model;
        return this;
    }

    @JSInfo("""
        Modifies the block's item representation.""")
    public BlockBuilder item(@Nullable Consumer<BlockItemBuilder> i) {
        if (i == null) {
            itemBuilder = null;
            lootTable = null;
        } else {
            i.accept(itemBuilder);
        }

        return this;
    }

    @JSInfo("""
        Set the block to have no corresponding item.""")
    public BlockBuilder noItem() {
        return item(null);
    }

    @Deprecated
    @JSInfo("use `box(...)` instead")
    public BlockBuilder shapeCube(double x0, double y0, double z0, double x1, double y1, double z1) {
        return box(x0, y0, z0, x1, y1, z1, true);
    }

    @JSInfo("Set the shape of the block.")
    public BlockBuilder box(double x0, double y0, double z0, double x1, double y1, double z1, boolean scale16) {
        if (scale16) {
            customShape.add(new AABB(x0 / 16D, y0 / 16D, z0 / 16D, x1 / 16D, y1 / 16D, z1 / 16D));
        } else {
            customShape.add(new AABB(x0, y0, z0, x1, y1, z1));
        }

        return this;
    }

    @JSInfo("Set the shape of the block.")
    public BlockBuilder box(double x0, double y0, double z0, double x1, double y1, double z1) {
        return box(x0, y0, z0, x1, y1, z1, true);
    }

    public static VoxelShape createShape(List<AABB> boxes) {
        if (boxes.isEmpty()) {
            return Shapes.block();
        }

        var shape = Shapes.create(boxes.get(0));

        for (int i = 1; i < boxes.size(); i++) {
            shape = Shapes.or(shape, Shapes.create(boxes.get(i)));
        }

        return shape;
    }

    public VoxelShape createShape() {
        return createShape(this.customShape);
    }

    @JSInfo("Makes the block not collide with entities.")
    public BlockBuilder noCollission() {
        noCollission = true;
        return this;
    }

    @JSInfo("Makes the block not be solid.")
    public BlockBuilder notSolid() {
        notSolid = true;
        return this;
    }

    @JSInfo("""
        Add a blockstate property to the block.
        
        For example, facing, lit, etc.""")
    public BlockBuilder property(Property<?> property) {
        if (property.getPossibleValues().size() <= 1) {
            throw new IllegalArgumentException(String.format(
                "Block \"%s\" has an illegal Blockstate Property \"%s\" which has <= 1 possible values. (%d possible values)",
                id,
                property.getName(),
                property.getPossibleValues().size()
            ));
        }
        blockStateProperties.add(property);
        return this;
    }

    @Deprecated
    public BlockBuilder setWaterlogged(boolean waterlogged) {
        ScriptType.STARTUP.console.warn(
            "\"BlockBuilder.waterlogged\" is a deprecated property! Please use \"BlockBuilder.property(BlockProperties.WATERLOGGED)\" instead.");
        if (waterlogged) {
            property(BlockStateProperties.WATERLOGGED);
        }
        return this;
    }

    @Deprecated
    public boolean getWaterlogged() {
        ScriptType.STARTUP.console.warn(
            "\"BlockBuilder.waterlogged\" is a deprecated property! Please use \"BlockBuilder.property(BlockProperties.WATERLOGGED)\" instead.");
        return canBeWaterlogged();
    }

    @JSInfo("Makes the block can be waterlogged.")
    public BlockBuilder waterlogged() {
        return property(BlockStateProperties.WATERLOGGED);
    }

    @JSInfo("Checks if the block can be waterlogged.")
    public boolean canBeWaterlogged() {
        return blockStateProperties.contains(BlockStateProperties.WATERLOGGED);
    }

    public BlockBuilder noDrops() {
        lootTable = null;
        return this;
    }

    public BlockBuilder slipperiness(float f) {
        slipperiness = f;
        return this;
    }

    public BlockBuilder speedFactor(float f) {
        speedFactor = f;
        return this;
    }

    public BlockBuilder jumpFactor(float f) {
        jumpFactor = f;
        return this;
    }

    /**
     * Sets random tick callback for this black.
     *
     * @param randomTickCallback A callback using a block container and a random.
     */
    public BlockBuilder randomTick(@Nullable Consumer<RandomTickCallbackJS> randomTickCallback) {
        this.randomTickCallback = randomTickCallback;
        return this;
    }

    public BlockBuilder noValidSpawns(boolean b) {
        noValidSpawns = b;
        return this;
    }

    public BlockBuilder suffocating(boolean b) {
        suffocating = b;
        return this;
    }

    public BlockBuilder viewBlocking(boolean b) {
        viewBlocking = b;
        return this;
    }

    public BlockBuilder redstoneConductor(boolean b) {
        redstoneConductor = b;
        return this;
    }

    public BlockBuilder transparent(boolean b) {
        transparent = b;
        return this;
    }

    public BlockBuilder defaultCutout() {
        return renderType("cutout").notSolid()
            .noValidSpawns(true)
            .suffocating(false)
            .viewBlocking(false)
            .redstoneConductor(false)
            .transparent(true);
    }

    public BlockBuilder defaultTranslucent() {
        return defaultCutout().renderType("translucent");
    }

    @JSInfo("Tags the block with the given tag.")
    public BlockBuilder tag(ResourceLocation tag) {
        super.tag(tag);
        return this;
    }

    @JSInfo("same as `tag()`")
    public BlockBuilder tagBlock(ResourceLocation tag) {
        return tag(tag);
    }

    @JSInfo("Tags the item with the given tag.")
    public BlockBuilder tagItem(ResourceLocation tag) {
        itemBuilder.tag(tag);
        return this;
    }

    @JSInfo("Tags both the block and the item with the given tag.")
    public BlockBuilder tagBlockAndItem(ResourceLocation tag) {
        tag(tag);
        tagItem(tag);
        return this;
    }

    @JSInfo("same as `tagBlockAndItem()`")
    public BlockBuilder tagBoth(ResourceLocation tag) {
        return tagBlockAndItem(tag);
    }

    @Deprecated
    @HideFromJS
    public BlockBuilder tagBlockAndItem(String tag) {
        return tagBlockAndItem(new ResourceLocation(tag));
    }

    public Block.Properties createProperties() {
        val properties = new KubeJSBlockProperties(this);

        properties.sound(material.getSound());

        if (resistance >= 0F) {
            properties.strength(hardness, resistance);
        } else {
            properties.strength(hardness);
        }

        properties.lightLevel(state -> (int) (lightLevel * 15F));

        if (harvestTool != null && harvestLevel >= 0 && properties instanceof BlockPropertiesExtension ex) {
            ex.tool(harvestTool, harvestLevel);
        }

        if (noCollission) {
            properties.noCollission();
        }

        if (notSolid) {
            properties.noOcclusion();
        }

        if (requiresTool) {
            properties.requiresCorrectToolForDrops();
        }

        if (lootTable == null) {
            properties.noDrops();
        }

        properties.friction(slipperiness);
        properties.speedFactor(speedFactor);
        properties.jumpFactor(jumpFactor);

        if (noValidSpawns) {
            properties.isValidSpawn((blockState, blockGetter, blockPos, object) -> false);
        }

        if (!suffocating) {
            properties.isSuffocating((blockState, blockGetter, blockPos) -> false);
        }

        if (!viewBlocking) {
            properties.isViewBlocking((blockState, blockGetter, blockPos) -> false);
        }

        if (!redstoneConductor) {
            properties.isRedstoneConductor((blockState, blockGetter, blockPos) -> false);
        }

        if (randomTickCallback != null) {
            properties.randomTicks();
        }

        return properties;
    }

    /**
     * same as {@link BlockBuilder#get()}
     */
    public Block getBlock() {
        return get();
    }

    @JSInfo("""
        I'm curious now, why call this method instead of using event.createCustom(...)?""")
    @Deprecated
    public void setBlock(Block block) {
        this.object = block;
    }

    public BlockBuilder setBlockstateJson(JsonObject blockstateJson) {
        this.blockstateJson = blockstateJson;
        return this;
    }

    public BlockBuilder setModelJson(JsonObject modelJson) {
        this.modelJson = modelJson;
        return this;
    }
}