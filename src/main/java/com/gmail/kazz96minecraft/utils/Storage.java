package com.gmail.kazz96minecraft.utils;

import com.gmail.kazz96minecraft.elements.Warp;
import com.gmail.kazz96minecraft.elements.serializers.WarpSerializer;
import org.spongepowered.api.Sponge;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Storage {

    public static File mapsDirectory;
    public static File warpsDirectory;
    static File backupsDirectory;
    static File worldsDirectory;

    public static void init() {
        initBackupFolders();
        initMapFolder();
        initSignFolder();
    }

    private static void initBackupFolders() {
        backupsDirectory = new File("mods/closedcombat/backups");
        worldsDirectory = new File(Sponge.getGame().getGameDirectory().toAbsolutePath() + File.separator + Sponge.getServer().getDefaultWorldName());

        if (!backupsDirectory.isDirectory()) {
            backupsDirectory.mkdirs();
        }
    }

    private static void initMapFolder() {
        mapsDirectory = new File("mods/closedcombat/maps");

        if (!mapsDirectory.isDirectory()) {
            mapsDirectory.mkdirs();
        }
    }

    private static void initSignFolder() {
        warpsDirectory = new File("mods/closedcombat/warps");

        if (!warpsDirectory.isDirectory()) {
            warpsDirectory.mkdirs();
        }
    }

    public static void deleteWarpFile(Warp warp) {
        File[] jsonFiles = Storage.warpsDirectory.listFiles((dir, name) -> name.equals(WarpSerializer.getJsonFileName(warp) + ".json"));

        if (jsonFiles == null || jsonFiles.length < 1){
            return;
        }

        jsonFiles[0].delete();
    }
}
