package com.gmail.kazz96minecraft.utils;

import com.gmail.kazz96minecraft.ClosedCombat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {

    public static void zipWorld(String worldName) throws Exception {
        Path sourceFolderPath = Paths.get(Storage.worldsDirectory.getPath(), worldName);
        Path zipPath = Paths.get(Storage.backupsDirectory.getPath(), worldName + ".zip");

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!(file.getFileName().toString().contains("level_sponge.dat") ||
                        file.getFileName().toString().contains("level_sponge.dat_old"))) {
                    zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }

    public static void unzipWorld(String worldName) {
        Path sourceFolderPath = Paths.get(Storage.worldsDirectory.getPath(), worldName);
        Path zipPath = Paths.get(Storage.backupsDirectory.getPath(), worldName + ".zip");

        File worldFolder = new File(sourceFolderPath.toString());

        if (!worldFolder.exists()) {
            worldFolder.mkdirs();
        } else {
            worldFolder.delete();
            worldFolder.mkdirs();
        }
        FileInputStream fileInputStream;

        byte[] buffer = new byte[1024];
        try {
            fileInputStream = new FileInputStream(zipPath.toString());
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(sourceFolderPath.toString() + File.separator + fileName);

                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            ClosedCombat.getInstance().getLogger().error("An error occurs while extracting " + worldName, e);
        }
    }

    public static boolean doesBackupExists(String worldName) {
        return new File(Storage.backupsDirectory.getPath(), worldName + ".zip").exists();
    }

    public static boolean doesWorldExists(String worldName) {
        return new File(Storage.worldsDirectory.getPath(), worldName).exists();
    }
}
