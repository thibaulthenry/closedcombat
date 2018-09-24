package com.gmail.kazz96minecraft.commands.world;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.utils.WorldDatas;
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
import java.time.Instant;

public class Import extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        String worldName = arguments.<String>getOne("world").orElseThrow(supplyError("Please insert a valid world name"));
        WorldDatas mapDatas = new WorldDatas(worldName);

        if (!mapDatas.levelDatExists()) {
            throw new CommandException(Text.of("Unable to find the ", worldName, "'s level.dat file"));
        }

        if (mapDatas.levelSpongeDatExists()) {
            throw new CommandException(Text.of(worldName, " is already imported"));
        }

        WorldArchetype.Builder worldArchetypeBuilder = WorldArchetype.builder()
                .dimension(DimensionTypes.OVERWORLD)
                .generator(mapDatas.getGeneratorType())
                .enabled(true)
                .generateSpawnOnLoad(false)
                .keepsSpawnLoaded(true)
                .loadsOnStartup(false);

        WorldArchetype settings = worldArchetypeBuilder.build(worldName + Instant.now(), worldName);

        WorldProperties worldProperties;
        try {
            worldProperties = Sponge.getServer().createWorldProperties(worldName, settings);
        } catch (IOException e) {
            throw new CommandException(Text.of("An error occurs while creating ", worldName, "'s properties"), e);
        }

        Sponge.getServer().saveWorldProperties(worldProperties);

        source.sendMessage(Text.of(TextColors.GREEN, worldName, " has been imported successfully"));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.world.import")
                .description(Text.of("Import a world from Minecraft world folder"))
                .arguments(GenericArguments.string(Text.of("world")))
                .executor(instance)
                .build();
    }
}
