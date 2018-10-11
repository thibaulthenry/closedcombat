package com.gmail.kazz96minecraft.commands.map;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.elements.Game;
import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Stop extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        Map map = arguments.<Map>getOne("map").orElseThrow(supplyError("Please insert a valid map name"));

        if (!Game.isRunning(map)) {
            throw new CommandException(Text.of("There is no game running on this map"));
        }

        if (!Game.get(map).isPresent()) {
            throw new CommandException(Text.of("The linked map to the game is unreachable"));
        }

        Game.get(map).get().destroy();
        source.sendMessage(Text.of(TextColors.GREEN, "The game on ", map.getName(), " has been stopped successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.map.stop")
                .description(Text.of("Stop a Closed Combat game"))
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
