package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.commands.Commands;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Unload extends AbstractCommand {
    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (!arguments.<String>getOne("world-name").isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, arguments, " plz worldname"));
        }
        String worldName = arguments.<String>getOne("world-name").get();

        Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(worldName);

        if (!optionalProperties.isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"));
        }
        WorldProperties properties = optionalProperties.get();

        Optional<World> optionalWorld = Sponge.getServer().getWorld(worldName);

        if (!optionalWorld.isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, worldName, " is already unloaded"), false);
        }

        World world = optionalWorld.get();

        if (Sponge.getServer().getDefaultWorldName().equals(worldName)) {
            throw new CommandException(Text.of(TextColors.RED, "You cannot unload the default world"), false);
        }

        World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorld().get().getWorldName()).get();

        world.getProperties().setLoadOnStartup(false);
        world.getProperties().setEnabled(false);

        world.getEntities().stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .forEach(player -> player.setLocation(defaultWorld.getSpawnLocation()));

        if (!Sponge.getServer().unloadWorld(world)) {
            throw new CommandException(Text.of(TextColors.RED, "Could not unload ", properties.getWorldName()), false);
        }

        try {
            world.save();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException(Text.of(TextColors.RED, "Saving world before unload faileeed"), false);
        }




        source.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " unloaded successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.usage.world.unload")
                .description(Text.of("Unload World"))
                .arguments(GenericArguments.string(Text.of("world-name")))
                .executor(Commands.UNLOAD.get())
                .build();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("unload", "uld");
    }
}
