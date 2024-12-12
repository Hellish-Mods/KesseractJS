package dev.latvian.kubejs.world.gen;

import lombok.val;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class AllRuleTest extends RuleTest {
	private final List<RuleTest> list;

	public AllRuleTest(List<RuleTest> rules) {
		list = new ArrayList<>(rules);
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		for (val test : list) {
			if (!test.test(blockState, random)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.ALWAYS_TRUE_TEST;
	}
}
