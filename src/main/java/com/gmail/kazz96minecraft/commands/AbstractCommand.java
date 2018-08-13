package com.gmail.kazz96minecraft.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.function.Supplier;


public abstract class AbstractCommand implements CommandExecutor {

    protected final Supplier<CommandException> errorBySponge = () -> new CommandException(Text.of("Error message handled by Sponge"));

    protected final AbstractCommand instance;

    protected AbstractCommand() {
        instance = this;
    }

    @SuppressWarnings("NullableProblems")
    public abstract CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException;

    public abstract CommandSpec getCommandSpec();
}
