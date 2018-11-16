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

public class Delete extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        Map map = arguments.<Map>getOne("map").orElseThrow(supplyError("Please insert a valid map name"));

        if (Game.get(map).isPresent()) {
            Game.get(map).get().destroy();
        }

        if (MapSerializer.getInstance().removeRegisteredMap(map)) {
            source.sendMessage(Text.of(TextColors.GREEN, "The map ", map.getName(), " has been deleted successfully"));
        } else {
            source.sendMessage(Text.of(TextColors.RED, "An error occurred while deleting the map ", map.getName()));
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.map.delete")
                .description(Text.of("Delete a Closed Combat game"))
                .arguments(
                        GenericArguments.choices(Text.of("map"),
                                () -> MapSerializer.getInstance().getHashMap().keySet(),
                                key -> MapSerializer.getInstance().getHashMap().get(key)
                        )
                )
                .executor(instance)
                .build();
    }
}
