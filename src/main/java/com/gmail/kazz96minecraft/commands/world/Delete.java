package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.concurrent.ExecutionException;

public class Delete extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        WorldProperties worldProperties = arguments.<WorldProperties>getOne("world").orElseThrow(() -> new CommandException(Text.of("Error message handled by Sponge")));
        String worldName = worldProperties.getWorldName();

        if (Sponge.getServer().getWorld(worldName).isPresent()) {
            throw new CommandException(Text.of(worldName, " must be unloaded before you can delete"));
        }

        try {
            Sponge.getServer().deleteWorld(worldProperties).get();
            source.sendMessage(Text.of(TextColors.GREEN, worldName, " has been deleted successfully"));

            return CommandResult.success();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new CommandException(Text.of("An error occurs while deleting ", worldName));
        }
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.world.delete")
                .description(Text.of("Delete World"))
                .arguments(GenericArguments.world(Text.of("world")))
                .executor(instance)
                .build();
    }
}
