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

        if (!(source instanceof Player)) {
            throw new CommandException(Text.of("You must be a player to join this map"));
        }

        Player player = (Player) source;

        if (Game.isRunning(map)) {
            throw new CommandException(Text.of("A game is already running on this map"));
        }

        Game game = Game.getOrCreate(map);

        if (game.isPlaying(player)) {
            throw new CommandException(Text.of("You are already registered on this map"));
        }

        if (game.isFull()) {
            throw new CommandException(Text.of("Too many players registered on this map"));
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
                        )
                )
                .executor(instance)
                .build();
    }
}
