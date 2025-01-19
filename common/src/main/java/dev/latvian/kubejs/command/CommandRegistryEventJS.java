package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS {
	public final CommandDispatcher<CommandSourceStack> dispatcher;
	public final Commands.CommandSelection selection;

	public CommandRegistryEventJS(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
		this.dispatcher = dispatcher;
		this.selection = selection;
	}

	public boolean isForSinglePlayer() {
		return selection.includeIntegrated;
	}

	public boolean isForMultiPlayer() {
		return selection.includeDedicated;
	}

	public CommandDispatcher<CommandSourceStack> getDispatcher() {
		return dispatcher;
	}

	public LiteralCommandNode<CommandSourceStack> register(final LiteralArgumentBuilder<CommandSourceStack> command) {
		return dispatcher.register(command);
	}

	public Class<Commands> getCommands() {
		return Commands.class;
	}

	public Class<ArgumentTypeWrapper> getArguments() {
		return ArgumentTypeWrapper.class;
	}

	// Used to access the static members of SharedSuggestionProvider
	// can be used within commands like so:
	// [cmd] .suggests((cx, builder) => event.builtinSuggestions.suggest(["123", "456"], builder))
	public Class<SharedSuggestionProvider> getBuiltinSuggestions() {
		return SharedSuggestionProvider.class;
	}
}