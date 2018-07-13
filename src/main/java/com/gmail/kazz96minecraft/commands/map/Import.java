package com.gmail.kazz96minecraft.commands.map;

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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Import extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        if(!arguments.<String>getOne("mapName").isPresent()) {
            throw new CommandException(Text.of("todo pas du tout d'arg")/*TODO*/);
        }

        MapDatas mapDatas = new MapDatas(arguments.<String>getOne("mapName").get());
        String mapName = mapDatas.getMapName();

        if (Sponge.getServer().getWorld(mapName).isPresent()) {
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
                .keepsSpawnLoaded(true)
                .loadsOnStartup(true);

        WorldArchetype settings = mapBuilder.build(mapName, mapName);

        WorldProperties properties;
        try {
            properties = Sponge.getServer().createWorldProperties(mapName, settings);
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
                .permission("closedcombat.import")
                .description(Text.of("Import Closed Combat Map"))
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("mapName"))))
                .executor(Commands.IMPORT.get())

                .build();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("import", "imp");
    }
}
