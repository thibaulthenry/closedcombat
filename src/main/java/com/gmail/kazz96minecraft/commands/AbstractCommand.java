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

    protected final AbstractCommand instance;

    protected AbstractCommand() {
        instance = this;
    }

    @SuppressWarnings("NullableProblems")
    public abstract CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException;

    @SuppressWarnings("unused")
    public abstract CommandSpec getCommandSpec();

    protected Supplier<CommandException> supplyError(String text) {
        return () -> new CommandException(Text.of(text));
    }
}
