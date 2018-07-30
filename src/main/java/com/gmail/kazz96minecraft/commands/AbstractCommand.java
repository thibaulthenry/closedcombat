package com.gmail.kazz96minecraft.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;


public abstract class AbstractCommand implements CommandExecutor {

    protected AbstractCommand instance;

    protected AbstractCommand() {
        instance = this;
    }

    @SuppressWarnings("NullableProblems")
    public abstract CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException;

    public abstract CommandSpec getCommandSpec();
}
