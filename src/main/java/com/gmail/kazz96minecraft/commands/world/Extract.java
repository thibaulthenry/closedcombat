package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.commands.Commands;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Extract extends AbstractCommand {
    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (!arguments.<String>getOne("world-name").isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, arguments, " plz worldname"));
        }
        String worldName = arguments.<String>getOne("world-name").get();

        if (!Zip.doesBackupExists(worldName)) {
            throw new CommandException(Text.of("pas de backup"));
        }

        if (Sponge.getServer().getDefaultWorldName().equals(worldName)) {
            throw new CommandException(Text.of(TextColors.RED, "You cannot extract a default world backup"), false);
        }

        Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(worldName);

        if (optionalProperties.isPresent()) {
            Optional<World> optionalWorld = Sponge.getServer().getWorld(worldName);

            if (optionalWorld.isPresent()) {
                throw new CommandException(Text.of(TextColors.RED, worldName, " plz unload before extract"), false);
            }
        }

        Zip.unzipWorld(worldName);

        source.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " extracted successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.usage.world.extract")
                .description(Text.of("Extract Backuped World"))
                .arguments(GenericArguments.string(Text.of("world-name")))
                .executor(Commands.EXTRACT.get())
                .build();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("extract", "etc");
    }
}
