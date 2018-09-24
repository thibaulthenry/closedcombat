package com.gmail.kazz96minecraft.commands.map;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class Leave extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (!(source instanceof Player)) {
            throw new CommandException(Text.of("You must be a player to join this map"));
        }

        Player player = (Player) source;

        Optional<Game> optionalGame = Game.get(player);

        if (!optionalGame.isPresent()) {
            throw new CommandException(Text.of("You aren't registered on any map"));
        }

        Game game = optionalGame.get();

        if (game.isRunning()) {
            throw new CommandException(Text.of("You can't leave while the game is running"));
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
                .executor(instance)
                .build();
    }
}
