package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Tags;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.gen.AllRuleTest;
import dev.latvian.kubejs.world.gen.AlwaysFalseRuleTest;
import dev.latvian.kubejs.world.gen.AnyRuleTest;
import dev.latvian.kubejs.world.gen.InvertRuleTest;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@FunctionalInterface
public interface BlockStatePredicate {
	ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	boolean check(BlockState state);

	default boolean checkBlock(Block block) {
		return check(block.defaultBlockState());
	}

	@Nullable
	default RuleTest asRuleTest() {
		return null;
	}

	static BlockStatePredicate fromString(String s) {
		if (s.equals("*")) {
			return Simple.ALL;
		} else if (s.equals("-")) {
			return Simple.NONE;
		} else if (s.startsWith("#")) {
			return new TagMatch(Tags.block(new ResourceLocation(s.substring(1))));
		} else if (s.indexOf('[') != -1) {
			val state = UtilsJS.parseBlockState(s);

			if (state != Blocks.AIR.defaultBlockState()) {
				return new StateMatch(state);
			}
		} else {
			val block = RegistryInfos.BLOCK.getValue(new ResourceLocation(s));

			if (block != Blocks.AIR) {
				return new BlockMatch(block);
			}
		}

		return Simple.NONE;
	}

	static BlockStatePredicate of(Object o) {
		if (o == null || o == Simple.ALL) {
			return Simple.ALL;
		} else if (o == Simple.NONE) {
			return Simple.NONE;
		}

		val list = ListJS.orSelf(o);
		if (list.isEmpty()) {
			return Simple.NONE;
		} else if (list.size() > 1) {
			val predicates = new ArrayList<BlockStatePredicate>();

			for (val o1 : list) {
				val p = of(o1);
				if (p == Simple.ALL) {
					return Simple.ALL;
				} else if (p != Simple.NONE) {
					predicates.add(p);
				}
			}

			return predicates.isEmpty() ? Simple.NONE : predicates.size() == 1 ? predicates.getFirst() : new OrMatch(predicates);
		}

		val map = MapJS.of(o);
		if (map != null) {
			if (map.isEmpty()) {
				return Simple.ALL;
			}

			val predicates = new ArrayList<BlockStatePredicate>();

			if (map.get("or") != null) {
				predicates.add(of(map.get("or")));
			}

			if (map.get("not") != null) {
				predicates.add(new NotMatch(of(map.get("not"))));
			}

			return new AndMatch(predicates);
		}

		return ofSingle(o);
	}

//	static RuleTest ruleTestOf(Object o) {
//		if (o instanceof RuleTest rule) {
//			return rule;
//		} else if (o instanceof BlockStatePredicate bsp && bsp.asRuleTest() != null) {
//			return bsp.asRuleTest();
//		}
//
//		return Optional.ofNullable(NBTUtils.toTagCompound(o))
//			.map(tag -> RuleTest.CODEC.parse(NbtOps.INSTANCE, tag))
//			.flatMap(DataResult::result)
//			.or(() -> Optional.ofNullable(of(o).asRuleTest()))
//			.orElseThrow(() -> new IllegalArgumentException("Could not parse valid rule test from " + o + "!"));
//	}

	@SuppressWarnings("unchecked")
	static BlockStatePredicate ofSingle(Object o) {
		if (o instanceof BlockStatePredicate bsp) {
			return bsp;
		} else if (o instanceof Block block) {
			return new BlockMatch(block);
		} else if (o instanceof BlockState state) {
			return new StateMatch(state);
		} else if (o instanceof Tag<?> tag) {
			return new TagMatch((Tag<Block>) tag);
		}

		val pattern = UtilsJS.parseRegex(o);
		return pattern == null ? BlockStatePredicate.fromString(o.toString()) : new RegexMatch(pattern);
	}

	default Collection<BlockState> getBlockStates() {
		val states = new HashSet<BlockState>();
		for (val state : UtilsJS.getAllBlockStates()) {
			if (check(state)) {
				states.add(state);
			}
		}
		return states;
	}

	default Collection<Block> getBlocks() {
		val blocks = new HashSet<Block>();
		for (val state : getBlockStates()) {
			blocks.add(state.getBlock());
		}
		return blocks;
	}

	default Set<ResourceLocation> getBlockIds() {
		val set = new LinkedHashSet<ResourceLocation>();

		for (val block : getBlocks()) {
			val blockId = RegistryInfos.BLOCK.getId(block);
			if (blockId != null) {
				set.add(blockId);
			}
		}

		return set;
	}

	enum Simple implements BlockStatePredicate {
		ALL(true),
		NONE(false);

		private final boolean match;

		Simple(boolean match) {
			this.match = match;
		}

		@Override
		public boolean check(BlockState state) {
			return match;
		}

		@Override
		public boolean checkBlock(Block block) {
			return match;
		}

		@Override
		public RuleTest asRuleTest() {
			return match ? AlwaysTrueTest.INSTANCE : AlwaysFalseRuleTest.INSTANCE;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return match ? UtilsJS.getAllBlockStates() : Collections.emptyList();
		}
	}

	record BlockMatch(Block block) implements BlockStatePredicate {
		@Override
		public boolean check(BlockState state) {
			return state.is(block);
		}

		@Override
		public boolean checkBlock(Block block) {
			return this.block == block;
		}

		@Override
		public Collection<Block> getBlocks() {
			return Collections.singleton(block);
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return block.getStateDefinition().getPossibleStates();
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			val blockId = RegistryInfos.BLOCK.getId(block);
			return blockId == null ? Collections.emptySet() : Collections.singleton(blockId);
		}

		@Override
		public RuleTest asRuleTest() {
			return new BlockMatchTest(block);
		}
	}

	record StateMatch(BlockState state) implements BlockStatePredicate {
		@Override
		public boolean check(BlockState s) {
			return state == s;
		}

		@Override
		public boolean checkBlock(Block block) {
			return state.getBlock() == block;
		}

		@Override
		public Collection<Block> getBlocks() {
			return Collections.singleton(state.getBlock());
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return Collections.singleton(state);
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			val blockId = RegistryInfos.BLOCK.getId(state.getBlock());
			return blockId == null ? Collections.emptySet() : Collections.singleton(blockId);
		}

		@Override
		public RuleTest asRuleTest() {
			return new BlockStateMatchTest(state);
		}
	}

	record TagMatch(Tag<Block> tag) implements BlockStatePredicate {
		@Override
		public boolean check(BlockState state) {
			return state.is(tag);
		}

		@Override
		public boolean checkBlock(Block block) {
			return block.is(tag);
		}

		@Override
		public Collection<Block> getBlocks() {
			return Collections.unmodifiableList(tag.getValues());
		}

		@Override
		public RuleTest asRuleTest() {
			return new TagMatchTest(tag);
		}
	}

	final class RegexMatch implements BlockStatePredicate {
		public final Pattern pattern;
		private final LinkedHashSet<Block> matchedBlocks;

		public RegexMatch(Pattern p) {
			pattern = p;
			matchedBlocks = new LinkedHashSet<>();
			for (val state : UtilsJS.getAllBlockStates()) {
				val block = state.getBlock();
				if (!matchedBlocks.contains(block)
                    && pattern.matcher(RegistryInfos.BLOCK.getId(block).toString()).find()) {
					matchedBlocks.add(state.getBlock());
				}
			}
		}

		@Override
		public boolean check(BlockState state) {
			return matchedBlocks.contains(state.getBlock());
		}

		@Override
		public boolean checkBlock(Block block) {
			return matchedBlocks.contains(block);
		}

		@Override
		public Collection<Block> getBlocks() {
			return matchedBlocks;
		}

		@Override
		public RuleTest asRuleTest() {
			val test = new AnyRuleTest();
			for (val block : matchedBlocks) {
				test.list.add(new BlockMatchTest(block));
			}
			return test;
		}
	}

	record OrMatch(List<BlockStatePredicate> list) implements BlockStatePredicate {
		@Override
		public boolean check(BlockState state) {
			for (val predicate : list) {
				if (predicate.check(state)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean checkBlock(Block block) {
			for (val predicate : list) {
				if (predicate.checkBlock(block)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Collection<Block> getBlocks() {
			val set = new HashSet<Block>();

			for (val predicate : list) {
				set.addAll(predicate.getBlocks());
			}

			return set;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			val set = new HashSet<BlockState>();

			for (val predicate : list) {
				set.addAll(predicate.getBlockStates());
			}

			return set;
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			Set<ResourceLocation> set = new LinkedHashSet<>();

			for (val predicate : list) {
				set.addAll(predicate.getBlockIds());
			}

			return set;
		}

		@Override
		public RuleTest asRuleTest() {
			val test = new AnyRuleTest();
			for (val predicate : list) {
				test.list.add(predicate.asRuleTest());
			}
			return test;
		}
	}

	final class NotMatch implements BlockStatePredicate {
		private final BlockStatePredicate predicate;
		private final Collection<BlockState> cachedStates;

		public NotMatch(BlockStatePredicate predicate) {
			this.predicate = predicate;

			cachedStates = new LinkedHashSet<>();

			for (val entry : RegistryInfos.BLOCK.entrySet()) {
				for (val state : entry.getValue().getStateDefinition().getPossibleStates()) {
					if (!predicate.check(state)) {
						cachedStates.add(state);
					}
				}
			}
		}

		@Override
		public boolean check(BlockState state) {
			return !predicate.check(state);
		}

		@Override
		public boolean checkBlock(Block block) {
			return !predicate.checkBlock(block);
		}

		@Override
		public Collection<Block> getBlocks() {
			Set<Block> set = new HashSet<>();
			for (val blockState : getBlockStates()) {
				set.add(blockState.getBlock());
			}
			return set;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return cachedStates;
		}

		@Override
		public Set<ResourceLocation> getBlockIds() {
			val set = new HashSet<ResourceLocation>();

			for (val block : getBlocks()) {
				set.add(RegistryInfos.BLOCK.getId(block));
			}

			return set;
		}

		@Override
		public RuleTest asRuleTest() {
			return new InvertRuleTest(predicate.asRuleTest());
		}
	}

	final class AndMatch implements BlockStatePredicate {
		private final List<BlockStatePredicate> list;
		private final Collection<BlockState> cachedStates;

		public AndMatch(List<BlockStatePredicate> list) {
			this.list = list;
			cachedStates = new LinkedHashSet<>();

			for (val entry : RegistryInfos.BLOCK.entrySet()) {
				for (val state : entry.getValue().getStateDefinition().getPossibleStates()) {
                    boolean match = true;
					for (val predicate : list) {
						if (!predicate.check(state)) {
							match = false;
							break;
						}
					}
					if (match) {
						cachedStates.add(state);
					}
				}
			}
		}

		@Override
		public boolean check(BlockState state) {
			for (val predicate : list) {
				if (!predicate.check(state)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean checkBlock(Block block) {
			for (val predicate : list) {
				if (!predicate.checkBlock(block)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public Collection<Block> getBlocks() {
			Set<Block> set = new HashSet<>();
			for (val blockState : getBlockStates()) {
				set.add(blockState.getBlock());
			}
			return set;
		}

		@Override
		public Collection<BlockState> getBlockStates() {
			return cachedStates;
		}

		@Override
		public RuleTest asRuleTest() {
            val rules = new ArrayList<RuleTest>(list.size());
            for (val predicate : list) {
                rules.add(predicate.asRuleTest());
            }
            return new AllRuleTest(rules);
		}
	}
}
