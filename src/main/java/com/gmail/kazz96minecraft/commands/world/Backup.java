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
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public class Backup extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (!arguments.<String>getOne("world-name").isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, " plz mapname"));
        }

        String worldName = arguments.<String>getOne("world-name").get();

        if (!Zip.doesWorldExists(worldName)) {
            throw new CommandException(Text.of(TextColors.RED, worldName, " introuvable"), false);
        }

        Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(worldName);

        if (optionalProperties.isPresent()) {
            Optional<World> optionalWorld = Sponge.getServer().getWorld(worldName);

            if (optionalWorld.isPresent()) {
                throw new CommandException(Text.of(TextColors.RED, worldName, " plz unload before backup"), false);
            }
        }

        try {
            Zip.zipWorld(worldName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        source.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " backuped successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.usage.world.backup")
                .description(Text.of("Backup World"))
                .arguments(GenericArguments.string(Text.of("world-name")))
                .executor(instance)
                .build();
    }
}
