package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.google.common.collect.Iterables;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.stream.Collectors;

public class List extends AbstractCommand {
    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) {
        PaginationList.Builder builder = PaginationList.builder();

        Iterable<Text> loadedWorlds = Sponge.getServer().getWorlds().stream()
                .map(world -> world.getProperties().getWorldName())
                .map(this::mapNetherEnd)
                .map(worldName -> Text.builder(worldName)
                        .color(TextColors.GREEN)
                        .onHover(TextActions.showText(Text.of("Click to teleport yourself")))
                        .onClick(TextActions.runCommand("/cc map tp " + worldName))
                        .build()
                )
                .collect(Collectors.toList());


        Iterable<Text> unloadedWorlds = Sponge.getServer().getUnloadedWorlds().stream()
                .map(WorldProperties::getWorldName)
                .map(this::mapNetherEnd)
                .map(worldName -> Text.builder(worldName)
                        .color(TextColors.RED)
                        .build()
                )
                .collect(Collectors.toList());

        Iterable<Text> allWorlds = Iterables.concat(loadedWorlds, unloadedWorlds);

        if (Iterables.size(allWorlds) != 0) {
            Text title = Text.builder().append(
                    Text.of("Worlds ["),
                    Text.of(TextColors.GREEN, "Loaded"),
                    Text.of(" - "),
                    Text.of(TextColors.RED, "Unloaded"),
                    Text.of("]")).build();

            builder.title(title)
                    .contents(allWorlds)
                    .build()
                    .sendTo(source);
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.world.list")
                .description(Text.of("Show the list of loaded and unloaded worlds"))
                .executor(instance)
                .build();
    }

    private String mapNetherEnd(String worldName) {
        switch (worldName) {
            case "DIM-1":
                return "The Nether";
            case "DIM1":
                return "The End";
            default:
                return worldName;
        }
    }
}
