package com.gmail.kazz96minecraft.commands;

import com.gmail.kazz96minecraft.commands.world.Backup;
import com.gmail.kazz96minecraft.commands.world.Delete;
import com.gmail.kazz96minecraft.commands.world.Extract;
import com.gmail.kazz96minecraft.commands.world.Import;
import com.gmail.kazz96minecraft.commands.world.Load;
import com.gmail.kazz96minecraft.commands.world.Unload;
import com.gmail.kazz96minecraft.commands.player.Teleport;

public enum Commands {
    IMPORT(new Import()),
    DELETE(new Delete()),
    LOAD(new Load()),
    UNLOAD(new Unload()),
    BACKUP(new Backup()),
    EXTRACT(new Extract()),
    TELEPORT(new Teleport());

    private final AbstractCommand command;

    Commands(AbstractCommand command) {
        this.command = command;
    }

    public AbstractCommand get() {
        return command;
    }
}