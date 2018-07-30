package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.utils.MapDatas;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;

public class Import extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if (!arguments.<String>getOne("world-name").isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, arguments, " plz worldname"));
        }
        MapDatas mapDatas = new MapDatas(arguments.<String>getOne("world-name").get());
        String worldName = mapDatas.getMapName();

        if (Sponge.getServer().getWorld(worldName).isPresent()) {
            throw new CommandException(Text.of("todo already loaded")/*TODO*/);
        }

        if (!mapDatas.levelDatExists()) {
            throw new CommandException(Text.of("todo level.dat introuvable")/*TODO*/);
        }

        if (mapDatas.levelSpongeDatExists()) {
            source.sendMessage(Text.of("todo already imported")/*TODO*/);
            return CommandResult.success();
        }

        WorldArchetype.Builder mapBuilder = WorldArchetype.builder()
                .dimension(DimensionTypes.OVERWORLD)
                .generator(mapDatas.getGeneratorType())
                .enabled(true)
                .generateSpawnOnLoad(false)
                .keepsSpawnLoaded(true)
                .loadsOnStartup(false);

        WorldArchetype settings = mapBuilder.build(worldName, worldName);

        WorldProperties properties;
        try {
            properties = Sponge.getServer().createWorldProperties(worldName, settings);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException(Text.of("todo crash")/*TODO*/);
        }

        Sponge.getServer().saveWorldProperties(properties);

        source.sendMessage(Text.of("todo ok")/*TODO*/);

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.usage.world.import")
                .description(Text.of("Import World"))
                .arguments(GenericArguments.string(Text.of("world-name")))
                .executor(instance)
                .build();
    }
}
