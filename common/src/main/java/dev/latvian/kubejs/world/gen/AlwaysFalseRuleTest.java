package dev.latvian.kubejs.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.Random;

/**
 * @author ZZZank
 */
public final class AlwaysFalseRuleTest extends RuleTest {
    public static final AlwaysFalseRuleTest INSTANCE = new AlwaysFalseRuleTest();
    public static final Codec<AlwaysFalseRuleTest> CODEC = Codec.unit(() -> INSTANCE);
    public static final RuleTestType<AlwaysFalseRuleTest> TYPE = RuleTestType.register("kubejs:always_false", CODEC);

    private AlwaysFalseRuleTest() {
    }

    public boolean test(BlockState blockState, Random random) {
        return false;
    }

    protected RuleTestType<?> getType() {
        return RuleTestType.ALWAYS_TRUE_TEST;
    }
}
