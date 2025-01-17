package dev.latvian.kubejs.block.custom.builder;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.block.RandomTickCallbackJS;
import dev.latvian.kubejs.block.custom.BasicCropBlockJS;
import dev.latvian.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.custom.SeedItemBuilder;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class CropBlockBuilder extends BlockBuilder {
	@FunctionalInterface
	public interface SurviveCallback {
		boolean survive(BlockState state, LevelReader reader, BlockPos pos);
	}

	public static class ShapeBuilder {
		private final List<VoxelShape> shapes;

		public ShapeBuilder(int age) {
			this.shapes = new ArrayList<>(Collections.nCopies(age + 1, Block.box(0.0d, 0.0d, 0.0d, 16.0d, 16.0d, 16.0d)));
		}

		@JSInfo("""
			Describe the shape of the crop at a specific age.
			
			min/max coordinates are double values between 0 and 16.
			""")
		public ShapeBuilder shape(int age, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
			shapes.set(age, Block.box(minX, minY, minZ, maxX, maxY, maxZ));
			return this;
		}

		public List<VoxelShape> getShapes() {
			return new ArrayList<>(shapes);
		}
	}

	public transient int age;
	public transient List<VoxelShape> shapeByAge;
	public transient boolean dropSeed;
	public transient ToDoubleFunction<RandomTickCallbackJS> growSpeedCallback;
	public transient ToIntFunction<RandomTickCallbackJS> fertilizerCallback;
	public transient SurviveCallback surviveCallback;

	public transient List<Pair<Object, Double>> outputs;

	public CropBlockBuilder(ResourceLocation i) {
		super(i);
		age = 7;
		shapeByAge = Collections.nCopies(8, Block.box(0.0d, 0.0d, 0.0d, 16.0d, 16.0d, 16.0d));
		growSpeedCallback = null;
		fertilizerCallback = null;
		surviveCallback = null;
		renderType = "cutout";
        noCollission = true;
		itemBuilder = new SeedItemBuilder(newID("", "_seed"));
		itemBuilder.blockBuilder = this;
		hardness = 0.0f;
		resistance = 0.0f;
		dropSeed = true;
		outputs = new ArrayList<>();
		notSolid = true;

        this.material(MaterialListJS.INSTANCE.get("crop"));

		//This should work as a minimum crop-like table
		lootTable = loot -> {
			val condition = new JsonObject();
			condition.addProperty("condition", "minecraft:block_state_property");
			condition.addProperty("block", this.newID("", "").toString());
			val properties = new JsonObject();
			properties.addProperty("age", String.valueOf(this.age));
			condition.add("properties", properties);

			val function = new JsonObject();
			function.addProperty("function", "minecraft:apply_bonus");
			function.addProperty("enchantment", "minecraft:fortune");
			function.addProperty("formula", "minecraft:binomial_with_bonus_count");
			val parameters = new JsonObject();
			parameters.addProperty("extra", 3);
			parameters.addProperty("probability", 0.5714286); //Same as vanilla
			function.add("parameters", parameters);

			if (dropSeed) {
				loot.addPool(bonuses -> {
					bonuses.rolls = ConstantIntValue.exactly(1);
					bonuses.bonusRolls = ConstantIntValue.exactly(0);
					bonuses.addItem(new ItemStack(itemBuilder.get()))
						.addCondition(condition)
						.addFunction(function);
					bonuses.addItem(new ItemStack(itemBuilder.get()));
				});
			}

			for (Pair<Object, Double> output : outputs) {
                loot.addPool(crops -> {
                    crops.rolls = ConstantIntValue.exactly(1);
                    crops.bonusRolls = ConstantIntValue.exactly(0);
                    crops.addItem(ItemStackJS.of(output.getFirst()).getItemStack())
                        .addCondition(condition)
                        .randomChance(output.getSecond())
                    ;
                });
			}
		};

		for (int a = 0; a <= age; a++) {
			texture(String.valueOf(a), id.getNamespace() + ":block/" + id.getPath() + a);
		}

		tag(BlockTags.CROPS.getName());
		if (Platform.isForge()) {
			tagItem(new ResourceLocation("forge", "seeds"));
		}
	}

	@JSInfo("Add a crop output with a 100% chance.")
	public CropBlockBuilder crop(Object output) {
		crop(output, 1.0);
		return this;
	}

	@JSInfo("Add a crop output with a specific chance.")
	public CropBlockBuilder crop(Object output, double chance) {
		outputs.add(new Pair<>(output, chance));
		return this;
	}

	@JSInfo("Set the age of the crop. Note that the box will be the same for all ages (A full block size).")
	public CropBlockBuilder age(int age) {
		age(age, (builder) -> {
		});
		return this;
	}

	@JSInfo("Set if the crop should drop seeds when harvested.")
	public CropBlockBuilder dropSeed(boolean dropSeed) {
		this.dropSeed = dropSeed;
		return this;
	}

	@JSInfo("Set the age of the crop and the shape of the crop at that age.")
	public CropBlockBuilder age(int age, Consumer<ShapeBuilder> builder) {
		this.age = age;
		ShapeBuilder shapes = new ShapeBuilder(age);
		builder.accept(shapes);
		this.shapeByAge = shapes.getShapes();
		for (int i = 0; i <= age; i++) {
			texture(String.valueOf(i), id.getNamespace() + ":block/" + id.getPath() + i);
		}
		return this;
	}

	@Override
	public BlockBuilder texture(String id, String tex) {
		try {
			int intId = (int) Double.parseDouble(id);
			return super.texture(String.valueOf(intId), tex);
		} catch (NumberFormatException e) {
			return super.texture(id, tex);
		}
	}

	public CropBlockBuilder bonemeal(ToIntFunction<RandomTickCallbackJS> bonemealCallback) {
		this.fertilizerCallback = bonemealCallback;
		return this;
	}

	public CropBlockBuilder survive(SurviveCallback surviveCallback) {
		this.surviveCallback = surviveCallback;
		return this;
	}

	public CropBlockBuilder growTick(ToDoubleFunction<RandomTickCallbackJS> growSpeedCallback) {
		this.growSpeedCallback = growSpeedCallback;
		return this;
	}

	@Override
	public BlockBuilder randomTick(@Nullable Consumer<RandomTickCallbackJS> randomTickCallback) {
		KubeJS.LOGGER.warn("randomTick is overridden by growTick to return grow speed, use it instead.");
		return this;
	}

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		for (int i = 0; i <= age; i++) {
            bs.variant(
                String.format("age=%s", i),
                model.isEmpty() ? (id.getNamespace() + ":block/" + id.getPath() + i) : model
            );
        }
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		for (int i = 0; i <= age; i++) {
			final int fi = i;
			generator.blockModel(newID("", String.valueOf(i)), m -> {
				m.parent("minecraft:block/crop");
				m.texture("crop", textures.get(String.valueOf(fi)).getAsString());
			});
		}

	}

	@Override
	public Block createObject() {
		IntegerProperty ageProperty = IntegerProperty.create("age", 0, age);
		return new BasicCropBlockJS(this) {
			@Override
			public IntegerProperty getAgeProperty() {
				/*
				 * Overriding getAgeProperty here because Minecraft calls getAgeProperty
				 * when CropBlock.class initializes. This happens when nothing is registered
				 * or assigned yet.
				 */
				return ageProperty;
			}

			@Override
			protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
				builder.add(ageProperty);
			}

			@Override
			public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
				double f = growSpeedCallback == null ? -1 : growSpeedCallback.applyAsDouble(new RandomTickCallbackJS(new BlockContainerJS(serverLevel, blockPos), random));
				int age = this.getAge(blockState);
				if (age < this.getMaxAge()) {
					if (f < 0) {
						f = getGrowthSpeed(this, serverLevel, blockPos);
					}
					if (f > 0 && random.nextInt((int) (25.0F / f) + 1) == 0) {
						serverLevel.setBlock(blockPos, this.getStateForAge(age + 1), 2);
					}
				}
			}

			@Override
			public void growCrops(Level level, BlockPos blockPos, BlockState blockState) {
				if (fertilizerCallback == null) {
					super.growCrops(level, blockPos, blockState);
				} else {
					int effect = fertilizerCallback.applyAsInt(new RandomTickCallbackJS(new BlockContainerJS(level, blockPos), level.random));
					if (effect > 0) {
						level.setBlock(blockPos, this.getStateForAge(Integer.min(getAge(blockState) + effect, getMaxAge())), 2);
					}
				}
			}

			@Override
			public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
				return surviveCallback != null ? surviveCallback.survive(blockState, levelReader, blockPos) : super.canSurvive(blockState, levelReader, blockPos);
			}
		};
	}
}
