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

public class Backup extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        String worldName = arguments.<String>getOne("world").orElseThrow(() -> new CommandException(Text.of("Error message handled by Sponge")));

        if (!Zip.doesWorldExists(worldName)) {
            throw new CommandException(Text.of("Unable to find ", worldName, "'s folder"));
        }

        if (Sponge.getServer().getWorld(worldName).isPresent()) {
            throw new CommandException(Text.of(worldName, " must be unloaded"));
        }

        try {
            Zip.zipWorld(worldName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommandException(Text.of("An error occurs while backuping ", worldName));
        }

        source.sendMessage(Text.of(TextColors.GREEN, worldName, " has been backuped successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.world.backup")
                .description(Text.of("Backup a world as zip file"))
                .arguments(GenericArguments.string(Text.of("world")))
                .executor(instance)
                .build();
    }
}
