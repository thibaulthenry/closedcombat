package com.gmail.kazz96minecraft.commands.map;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.elements.Game;
import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import com.gmail.kazz96minecraft.utils.Shortcuts;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

public class Update extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        Map oldMap = arguments.<Map>getOne("map").orElseThrow(supplyError("Please insert a valid map name"));

        if (!oldMap.getLinkedWorld().isPresent()) {
            throw new CommandException(Text.of("The linked world of ", oldMap.getName(), " isn't reachable anymore"));
        }

        if (Game.isRunning(oldMap)) {
            throw new CommandException(Text.of("A game is currently running for this map"));
        }

        if (Game.get(oldMap).isPresent()) {
            Game.get(oldMap).get().destroy();
        }

        MapSerializer.getInstance().getList().remove(oldMap);

        Map.leftBlockMarker = new Location<>(oldMap.getLinkedWorld().get(), oldMap.getLeftLimitPosition());
        Map.rightBlockMarker = new Location<>(oldMap.getLinkedWorld().get(), oldMap.getRightLimitPosition());

        CommandResult createCommand = Shortcuts.runCommand("cc", "map", "create", oldMap.getName());

        if (!createCommand.equals(CommandResult.success())) {
            throw new CommandException(Text.of("An error occurred while updating ", oldMap.getName(), " configuration file"));
        }

        source.sendMessage(Text.of(TextColors.GREEN, "The configuration file of ", oldMap.getName(), " has been updated successfully"));

        return createCommand;
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.map.update")
                .description(Text.of("Update Closed Combat map configuration file"))
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.choices(Text.of("map"),
                                        () -> MapSerializer.getInstance().getHashMap().keySet(),
                                        key -> MapSerializer.getInstance().getHashMap().get(key))
                        )
                )
                .executor(instance)
                .build();
    }
}
