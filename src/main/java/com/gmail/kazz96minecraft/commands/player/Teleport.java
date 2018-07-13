package com.gmail.kazz96minecraft.commands.player;

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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Teleport extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (!arguments.<String>getOne("mapName").isPresent()) {
            throw new CommandException(Text.of("todo pas du tout d'arg")/*TODO*/);
        }

        if (!(source instanceof Player)) {
            throw new CommandException(Text.of("todo usage")/*TODO*/);
        }
        Player player = (Player) source;
        String mapName = arguments.<String>getOne("mapName").get();

        Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(mapName);

        if (!optionalProperties.isPresent()) {
            throw new CommandException(Text.of("todo usage")/*TODO*/);
        }
        WorldProperties properties = optionalProperties.get();

        Optional<World> optionalWorld = Sponge.getServer().getWorld(mapName);

        if (!optionalWorld.isPresent()) {
            throw new CommandException(Text.of("todo usage")/*TODO*/);
        }
        World world = optionalWorld.get();

        Location<World> location = world.getSpawnLocation();
        player.setLocation(location);

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.tp")
                .description(Text.of("Teleport a player to a Closed Combat Map"))
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("mapName"))))
                .executor(Commands.TELEPORT.get())
                .build();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("teleport", "tp", "go");
    }
}
