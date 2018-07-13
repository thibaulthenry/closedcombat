package com.gmail.kazz96minecraft.commands;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CCRegister {

    private final HashMap<List<String>, CommandSpec> subCommands = new HashMap<>();
    private final CommandSpec commandSpec;

    public CCRegister() {
        Arrays.stream(Commands.values())
                .forEach(commands -> addCommand(commands.get()));

        commandSpec = CommandSpec.builder()
                .extendedDescription(Text.of("Closed Combat plugin commands"))
                .permission("closedcombat.usage")
                .children(subCommands)
                .build();
    }

    public CommandSpec getCommandSpec() {
        return commandSpec;
    }

    private void addCommand(AbstractCommand command) {
        subCommands.put(command.getAliases(), command.getCommandSpec());
    }
}
