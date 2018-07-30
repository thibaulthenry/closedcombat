package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.util.Optional;

public class Unload extends AbstractCommand {
    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        WorldProperties worldProperties = arguments.<WorldProperties>getOne("world").orElseThrow(() -> new CommandException(Text.of("Error message handled by Sponge")));
        String worldName = worldProperties.getWorldName();

        World world = Sponge.getServer().getWorld(worldProperties.getUniqueId()).orElseThrow(() -> new CommandException(Text.of(worldName, " isn't loaded")));

        if (Sponge.getServer().getDefaultWorldName().equals(worldName)) {
            throw new CommandException(Text.of("Default world cannot be unloaded"));
        }

        World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).orElseThrow(() -> new CommandException(Text.of("An error occurs while catching the default world")));

        world.getEntities().stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .forEach(player -> player.setLocation(defaultWorld.getSpawnLocation()));

        worldProperties.setLoadOnStartup(false);
        worldProperties.setEnabled(false);
        Sponge.getServer().saveWorldProperties(worldProperties);

        if (!Sponge.getServer().unloadWorld(world)) {
            throw new CommandException(Text.of("An error occurs while unloading ", worldName));
        }

        try {
            world.save();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException(Text.of("An error occurs while saving ", worldName, " after unloading"));
        }

        source.sendMessage(Text.of(TextColors.GREEN, worldName, " has been unloaded successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.world.unload")
                .description(Text.of("Unload an existing world"))
                .arguments(GenericArguments.string(Text.of("world")))
                .executor(instance)
                .build();
    }
}
