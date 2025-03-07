package dev.latvian.kubejs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Supplier;

/**
 * @author ZZZank
 */
class ArgumImpl<T extends ArgumentType<?>, O> implements ArgumentTypeWrapper<T, O> {

    private final Supplier<T> factory;
    private final ArgumentFunction<O> getter;

    static <T extends ArgumentType<?>, O> ArgumentTypeWrapper<T, O> of(Supplier<T> factory, ArgumentFunction<O> getter) {
        return new ArgumImpl<>(factory, getter);
    }

    ArgumImpl(Supplier<T> factory, ArgumentFunction<O> getter) {
        this.factory = factory;
        this.getter = getter;
    }

    @Override
    public T create(CommandRegistryEventJS event) {
        return factory.get();
    }

    @Override
    public O getResult(CommandContext<CommandSourceStack> context, String input) throws CommandSyntaxException {
        return getter.getResult(context, input);
    }
}
