package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.utils.Zip;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Extract extends AbstractCommand {
    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        String worldName = arguments.<String>getOne("world").orElseThrow(() -> new CommandException(Text.of("Error message handled by Sponge")));

        if (!Zip.doesBackupExists(worldName)) {
            throw new CommandException(Text.of("Unable to find ", worldName, "'s zip backup"));
        }

        if (Sponge.getServer().getDefaultWorldName().equals(worldName)) {
            throw new CommandException(Text.of("Default world cannot be overwritten with a backup"));
        }

        if (Sponge.getServer().getWorld(worldName).isPresent()) {
            throw new CommandException(Text.of(worldName, " must be unloaded"));
        }

        Zip.unzipWorld(worldName);

        source.sendMessage(Text.of(TextColors.GREEN, worldName, " has been extracted successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.world.extract")
                .description(Text.of("Extract Backuped World"))
                .arguments(GenericArguments.string(Text.of("world")))
                .executor(instance)
                .build();
    }
}
