package com.gmail.kazz96minecraft.commands.world;

import com.flowpowered.math.vector.Vector3d;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

public class Teleport extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        WorldProperties worldProperties = arguments.<WorldProperties>getOne("world").orElseThrow(supplyError("Please insert a valid world name"));

        World world = Sponge.getServer().getWorld(worldProperties.getUniqueId()).orElseThrow(supplyError("Please insert a valid world name"));

        Player player = arguments.<Player>getOne("player").orElseThrow(supplyError("Please insert a valid player name"));

        if (!arguments.<Double>getOne("x").isPresent()) {
            Location<World> spawnLocation = world.getSpawnLocation();
            player.setLocation(spawnLocation);
            return CommandResult.success();
        }

        double x = arguments.<Double>getOne("x").get();
        double y = arguments.<Double>getOne("y").orElseThrow(() -> new CommandException(Text.of("Missing <y> & <z> coordinates")));
        double z = arguments.<Double>getOne("z").orElseThrow(() -> new CommandException(Text.of("Missing <z> coordinates")));

        Location<World> location = world.getLocation(x, y, z);
        player.setLocation(location.add(0.5, 0, 0.5));

        double rx = arguments.<Double>getOne("rx").orElse(player.getRotation().getX());
        double ry = arguments.<Double>getOne("ry").orElse(player.getRotation().getY());
        double rz = arguments.<Double>getOne("rz").orElse(player.getRotation().getZ());

        player.setRotation(new Vector3d(rx, ry, rz));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.world.teleport")
                .description(Text.of("Teleport a player to a specified world"))
                .arguments(
                        GenericArguments.world(Text.of("world")),
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("x"))),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("y"))),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("z"))),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("rx"))),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("ry"))),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("rz")))
                )
                .executor(instance)
                .build();
    }
}
