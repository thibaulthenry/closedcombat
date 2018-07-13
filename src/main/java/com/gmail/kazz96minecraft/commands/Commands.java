package com.gmail.kazz96minecraft.commands;

import com.gmail.kazz96minecraft.commands.map.Import;
import com.gmail.kazz96minecraft.commands.map.Load;
import com.gmail.kazz96minecraft.commands.player.Teleport;

public enum Commands {
    IMPORT(new Import()),
    LOAD(new Load()),
    TELEPORT(new Teleport());

    private final AbstractCommand command;

    Commands(AbstractCommand command) {
        this.command = command;
    }

    public AbstractCommand get() {
        return command;
    }
}