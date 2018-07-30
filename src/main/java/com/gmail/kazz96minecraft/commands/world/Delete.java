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

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Delete extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (!arguments.<String>getOne("world-name").isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, " plz worldname"));
        }

        String worldName = arguments.<String>getOne("world-name").get();

        Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(worldName);

        if (!optionalWorld.isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, " does not exist"), false);
        }
        WorldProperties world = optionalWorld.get();

        if (Sponge.getServer().getWorld(world.getWorldName()).isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, worldName, " must be unloaded before you can delete"), false);
        }

        try {
            if (Sponge.getServer().deleteWorld(world).get()) {

                source.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " deleted successfully"));

                return CommandResult.success();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
        }

        source.sendMessage(Text.of(TextColors.RED, "Could not delete ", worldName));

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.usage.world.delete")
                .description(Text.of("Delete World"))
                .arguments(GenericArguments.string(Text.of("world-name")))
                .executor(instance)
                .build();
    }
}
