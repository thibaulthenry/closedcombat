package com.gmail.kazz96minecraft.commands.map;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.stream.Collectors;

public class Information extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (arguments.<Map>getOne("map").isPresent()) {
            source.sendMessage(getMapInformation(arguments.<Map>getOne("map").get()));

            return CommandResult.success();
        }

        PaginationList.Builder builder = PaginationList.builder();

        List<Text> registeredMaps = MapSerializer.getInstance().getList().stream()
                .map(map -> Text.builder(map.getName())
                        .color(TextColors.GREEN)
                        .onHover(TextActions.showText(getMapInformation(map)))
                        .build()
                )
                .collect(Collectors.toList());

        if (registeredMaps.size() != 0) {
            builder.title(Text.of("Closed Combat Maps"))
                    .contents(registeredMaps)
                    .build()
                    .sendTo(source);
        } else {
            throw new CommandException(Text.of("There is no Closed Combat map registered on this server"));
        }

        return CommandResult.success();
    }

    private Text getMapInformation(Map map) {
        return Text.builder()
                .append(Text.of("Linked world : "))
                .append(Text.of(TextColors.GRAY, map.getLinkedWorld().isPresent() ? map.getLinkedWorld().get().getName() : "None (unloaded or non-existent)", "\n"))
                .append(Text.of("Total warp signs : "))
                .append(Text.of(TextColors.GRAY, map.getWarpSignNumber(), "\n"))
                .append(Text.of("Spawn number : "))
                .append(Text.of(TextColors.GRAY, map.getSpawns().size(), "\n"))
                .append(Text.of("Total spawns capacity : "))
                .append(Text.of(TextColors.GRAY, map.getTotalSpawnsCapacity(), "\n"))
                .append(Text.of("Minimum players : "))
                .append(Text.of(TextColors.GRAY, map.getMinPlayers(), "\n"))
                .append(Text.of("Maximum players : "))
                .append(Text.of(TextColors.GRAY, map.getMaxPlayers(), "\n"))
                .append(Text.of("Countdown before start : "))
                .append(Text.of(TextColors.GRAY, map.getCountdown(), "\n"))
                .append(Text.of("Breakable blocks : "))
                .append(Text.of(TextColors.GRAY, map.getBreakableBlocks().size() == 0 ? "None" : map.getBreakableBlocks(), "\n"))
                .append(Text.of("Placeable blocks : "))
                .append(Text.of(TextColors.GRAY, map.getPlaceableBlocks().size() == 0 ? "None" : map.getPlaceableBlocks(), "\n"))
                .build();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.map.information")
                .description(Text.of("Show information about a registered Closed Combat map"))
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
