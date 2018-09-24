package com.gmail.kazz96minecraft.commands.map;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Create extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        String mapName = arguments.<String>getOne("name").orElseThrow(supplyError("Please insert a valid map name"));

        if (Map.leftBlockMarker == null || Map.rightBlockMarker == null) {
            throw new CommandException(Text.of("CCStick's markers must be set before creating a map"));
        }

        if (!Map.leftBlockMarker.getExtent().getName().equals(Map.rightBlockMarker.getExtent().getName())) {
            throw new CommandException(Text.of("CCStick's markers must be set in the same world"));
        }

        Task.builder().async().execute(() -> {
            Map map = new Map(mapName);

            if (!MapSerializer.getInstance().serialize(map)) {
                source.sendMessage(Text.of(TextColors.RED, "An error occurs while creating ", mapName, "'s properties file"));
                return;
            }

            MapSerializer.getInstance().getList().add(map);
            source.sendMessage(Text.of(TextColors.GREEN, mapName, " has been created successfully"));
        }).submit(ClosedCombat.getInstance());

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.map.create")
                .description(Text.of("Create a Closed Combat map"))
                .arguments(GenericArguments.string(Text.of("name")))
                .executor(instance)
                .build();
    }
}
