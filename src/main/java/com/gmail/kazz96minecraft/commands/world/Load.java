package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.utils.WorldDatas;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public class Load extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        WorldProperties worldProperties = arguments.<WorldProperties>getOne("world").orElseThrow(() -> new CommandException(Text.of("Error message handled by Sponge")));
        String worldName = worldProperties.getWorldName();
        WorldDatas mapDatas = new WorldDatas(worldName);

        if (Sponge.getServer().getWorld(worldProperties.getUniqueId()).isPresent()) {
            throw new CommandException(Text.of(worldName, " is already loaded"));
        }

        if (!mapDatas.levelDatExists()) {
            throw new CommandException(Text.of("Unable to find the ", worldName, "'s level.dat file"));
        }

        source.sendMessage(Text.of(TextColors.DARK_GRAY, worldName, " about to be loaded.."));

        Task.builder().delayTicks(20).execute(c -> {
            Optional<World> loadingWorld = Sponge.getServer().loadWorld(worldProperties);

            if (!loadingWorld.isPresent()) {
                source.sendMessage(Text.of(TextColors.RED, "An error occurs while loading", worldName));
                return;
            }

            worldProperties.setLoadOnStartup(true);
            Sponge.getServer().saveWorldProperties(worldProperties);

            source.sendMessage(Text.of(TextColors.GREEN, worldName, " has been loaded successfully"));
        }).submit(ClosedCombat.getInstance().getPlugin());

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.world.load")
                .description(Text.of("Load a world on the server"))
                .arguments(GenericArguments.world(Text.of("world")))
                .executor(instance)
                .build();
    }
}
