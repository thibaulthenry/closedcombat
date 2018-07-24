package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.commands.Commands;
import com.gmail.kazz96minecraft.utils.MapDatas;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Load extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (!arguments.<String>getOne("world-name").isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, arguments, " plz worldname"));
        }
        String worldName = arguments.<String>getOne("world-name").get();
        Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(worldName);

        if (!optionalWorld.isPresent()) {
            throw new CommandException(Text.of("todo usage")/*TODO*/);
        }
        WorldProperties world = optionalWorld.get();

        if (Sponge.getServer().getWorld(world.getUniqueId()).isPresent()) {
            throw new CommandException(Text.of("todo usage")/*TODO*/);
        }

        MapDatas mapDatas = new MapDatas(worldName);

        if (!mapDatas.levelDatExists()) {
            throw new CommandException(Text.of("todo usage")/*TODO*/);
        }

        if (!mapDatas.levelSpongeDatExists()) {
            source.sendMessage(Text.of("todo usage")/*TODO*/);
            return CommandResult.success();
        }

        source.sendMessage(Text.of("todo usage")/*TODO*/);

        Task.builder().delayTicks(20).execute(c -> {
            Optional<World> load = Sponge.getServer().loadWorld(world);

            if (!load.isPresent()) {
                source.sendMessage(Text.of("todo usage")/*TODO*/);
                return;
            }

            load.get().getProperties().setLoadOnStartup(true);

            try {
                load.get().save();
            } catch (IOException e) {
                e.printStackTrace();
            }

            source.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " loaded successfully"));
        }).submit(ClosedCombat.getInstance().getPlugin());

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.usage.world.load")
                .description(Text.of("Load World"))
                .arguments(GenericArguments.string(Text.of("world-name")))
                .executor(Commands.LOAD.get())
                .build();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("load", "ld");
    }
}