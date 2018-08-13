package com.gmail.kazz96minecraft.commands;

import com.gmail.kazz96minecraft.commands.map.Create;
import com.gmail.kazz96minecraft.commands.map.Give;
import com.gmail.kazz96minecraft.commands.map.Informations;
import com.gmail.kazz96minecraft.commands.world.Backup;
import com.gmail.kazz96minecraft.commands.world.Delete;
import com.gmail.kazz96minecraft.commands.world.Extract;
import com.gmail.kazz96minecraft.commands.world.Import;
import com.gmail.kazz96minecraft.commands.world.List;
import com.gmail.kazz96minecraft.commands.world.Load;
import com.gmail.kazz96minecraft.commands.world.Teleport;
import com.gmail.kazz96minecraft.commands.world.Unload;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandRegister {

    private final CommandSpec commandSpec;

    public CommandRegister() {
        CommandSpec worldCommandSpec = CommandSpec.builder()
                .description(Text.of("Closed Combat world commands"))
                .permission("closedcombat.world")
                .child(new Backup().getCommandSpec(), "backup", "zip")
                .child(new Delete().getCommandSpec(), "delete", "rm")
                .child(new Extract().getCommandSpec(), "extract", "unzip")
                .child(new Import().getCommandSpec(), "import", "imp")
                .child(new List().getCommandSpec(), "list", "ls")
                .child(new Load().getCommandSpec(), "load", "ld")
                .child(new Teleport().getCommandSpec(), "teleport", "tp", "go")
                .child(new Unload().getCommandSpec(), "unload", "uld")
                .build();

        CommandSpec mapCommandSpec = CommandSpec.builder()
                .description(Text.of("Closed Combat map commands"))
                .permission("closedcombat.map")
                .child(new Create().getCommandSpec(), "create", "c")
                .child(new Give().getCommandSpec(), "give")
                .child(new Informations().getCommandSpec(), "informations", "infos")
                .build();

        commandSpec = CommandSpec.builder()
                .description(Text.of("Closed Combat commands"))
                .permission("closedcombat")
                .child(worldCommandSpec, "world", "w")
                .child(mapCommandSpec, "map", "m")
                .build();
    }

    public CommandSpec getCommandSpec() {
        return commandSpec;
    }
}
