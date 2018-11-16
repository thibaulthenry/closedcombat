package com.gmail.kazz96minecraft.utils;

import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.elements.Warp;
import com.gmail.kazz96minecraft.elements.serializers.WarpSerializer;
import org.spongepowered.api.Sponge;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Storage {

    public static File mapsDirectory;
    public static File warpsDirectory;
    public static File statisticsDirectory;
    static File backupsDirectory;
    static File worldsDirectory;

    public static void init() {
        initBackupsFolders();
        initMapsFolder();
        initWarpsFolder();
        initStatisticsFolder();
    }

    private static void initBackupsFolders() {
        backupsDirectory = new File("mods/closedcombat/backups");
        worldsDirectory = new File(Sponge.getGame().getGameDirectory().toAbsolutePath() + File.separator + Sponge.getServer().getDefaultWorldName());

        if (!backupsDirectory.isDirectory()) {
            backupsDirectory.mkdirs();
        }
    }

    private static void initMapsFolder() {
        mapsDirectory = new File("mods/closedcombat/maps");

        if (!mapsDirectory.isDirectory()) {
            mapsDirectory.mkdirs();
        }
    }

    private static void initWarpsFolder() {
        warpsDirectory = new File("mods/closedcombat/warps");

        if (!warpsDirectory.isDirectory()) {
            warpsDirectory.mkdirs();
        }
    }

    private static void initStatisticsFolder() {
        statisticsDirectory = new File("mods/closedcombat/statistics");

        if (!statisticsDirectory.isDirectory()) {
            statisticsDirectory.mkdirs();
        }
    }

    public static boolean deleteWarpFile(Warp warp) {
        File[] jsonFiles = Storage.warpsDirectory.listFiles((dir, name) -> name.equals(WarpSerializer.getJsonFileName(warp) + ".json"));

        if (jsonFiles == null || jsonFiles.length < 1){
            return false;
        }

        return jsonFiles[0].delete();
    }

    public static boolean deleteMapFile(Map map) {
        File[] jsonFiles = Storage.mapsDirectory.listFiles((dir, name) -> name.equals(map.getName() + ".json"));

        if (jsonFiles == null || jsonFiles.length < 1) {
            return false;
        }

        return jsonFiles[0].delete();
    }
}
