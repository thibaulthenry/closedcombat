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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Join extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        Map map = arguments.<Map>getOne("map").orElseThrow(supplyError("Please insert a valid map name"));
        Player player = arguments.<Player>getOne("player").orElseThrow(supplyError("Please insert a valid player name"));

        if (Game.isRunning(map)) {
            player.sendMessage(Text.of(TextColors.RED, "A game is already running on this map"));
            return CommandResult.empty();
        }

        Game game = Game.getOrCreate(map);

        if (game.isFull()) {
            player.sendMessage(Text.of(TextColors.RED, "Too many players registered on this map"));
            return CommandResult.empty();
        }

        if (game.isStopped()) {
            player.sendMessage(Text.of(TextColors.RED, "The game is about to restart"));
            return CommandResult.empty();
        }

        if (game.isPlaying(player)) {
            player.sendMessage(Text.of(TextColors.RED, "You are already registered on a map"));
            return CommandResult.empty();
        }

        game.join(player);
        player.sendMessage(Text.of(TextColors.GREEN, "You have been registered on ", game.getLinkedMap().getName(), " successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.map.join")
                .description(Text.of("Join a Closed Combat game"))
                .arguments(
                        GenericArguments.choices(Text.of("map"),
                                () -> MapSerializer.getInstance().getHashMap().keySet(),
                                key -> MapSerializer.getInstance().getHashMap().get(key)
                        ),
                        GenericArguments.playerOrSource(Text.of("player"))
                )
                .executor(instance)
                .build();
    }
}
