package com.gmail.kazz96minecraft.utils;

import org.spongepowered.api.Sponge;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Storage {

    public static File mapsDirectory;
    static File backupsDirectory;
    static File worldsDirectory;

    public static void init() {
        initBackupFolders();
        initMapFolders();
    }

    private static void initMapFolders() {
        mapsDirectory = new File("mods/closedcombat/maps");

        if (!mapsDirectory.isDirectory()) {
            mapsDirectory.mkdirs();
        }
    }

    private static void initBackupFolders() {
        backupsDirectory = new File("mods/closedcombat/backups");
        worldsDirectory = new File(Sponge.getGame().getGameDirectory().toAbsolutePath() + File.separator + Sponge.getServer().getDefaultWorldName());

        if (!backupsDirectory.isDirectory()) {
            backupsDirectory.mkdirs();
        }
    }
}
