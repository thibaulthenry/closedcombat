package com.gmail.kazz96minecraft.commands;

import com.gmail.kazz96minecraft.commands.map.*;
import com.gmail.kazz96minecraft.commands.world.*;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandRegister {

    private final CommandSpec commandSpec;

    public CommandRegister() {
        CommandSpec worldCommandSpec = CommandSpec.builder()
                .description(Text.of("Closed Combat world commands"))
                .permission("closedcombat.world")
                .child(new Backup().getCommandSpec(), "backup", "zip")
                .child(new com.gmail.kazz96minecraft.commands.world.Delete().getCommandSpec(), "delete", "rm")
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
                .child(new com.gmail.kazz96minecraft.commands.map.Delete().getCommandSpec(), "delete", "rm")
                .child(new Give().getCommandSpec(), "give")
                .child(new Information().getCommandSpec(), "information", "info")
                .child(new Join().getCommandSpec(), "join")
                .child(new Leave().getCommandSpec(), "leave")
                .child(new Stop().getCommandSpec(), "stop")
                .child(new Update().getCommandSpec(), "update")
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
