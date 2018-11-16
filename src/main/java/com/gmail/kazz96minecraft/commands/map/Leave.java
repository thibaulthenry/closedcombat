package com.gmail.kazz96minecraft.commands.map;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class Leave extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        Player player = arguments.<Player>getOne("player").orElseThrow(supplyError("Please insert a valid player name"));
        Optional<Game> optionalGame = Game.get(player);

        if (!optionalGame.isPresent()) {
            player.sendMessage(Text.of(TextColors.RED, "You aren't registered on any map"));
            return CommandResult.empty();
        }

        Game game = optionalGame.get();

        if (game.isRunning()) {
            player.sendMessage(Text.of(TextColors.RED, "You can't leave while the game is running"));
            return CommandResult.empty();
        }

        game.leave(player);
        player.sendMessage(Text.of(TextColors.GREEN, "You have been unregistered from ", game.getLinkedMap().getName(), " successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.map.leave")
                .description(Text.of("Leave a Closed Combat game"))
                .arguments(
                        GenericArguments.playerOrSource(Text.of("player"))
                )
                .executor(instance)
                .build();
    }
}
