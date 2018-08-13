package com.gmail.kazz96minecraft.elements.serializers;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.utils.Storage;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapSerializer {

    private static List<Map> mapList = new ArrayList<>();

    private static Optional<Map> deserialize(File mapFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mapFile));

            Gson gson = new Gson();
            Map map = gson.fromJson(bufferedReader, Map.class);
            return Optional.of(map);
        } catch (FileNotFoundException e) {
            ClosedCombat.getInstance().getLogger().error("An error occurs while loading " + mapFile, e);
            return Optional.empty();
        }
    }

    public static boolean serialize(Map map) {
        try (Writer writer = new FileWriter(Paths.get(Storage.mapsDirectory.getPath(), map.getName() + ".json").toString())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(map, writer);

            mapList.add(map);

            return true;
        } catch (IOException e) {
            ClosedCombat.getInstance().getLogger().error("An error occurs while saving " + map.getName() + "'s properties file", e);
            return false;
        }
    }

    public static void loadMaps() {
        try {
            Arrays.stream(Objects.requireNonNull(Storage.mapsDirectory.listFiles()))
                    .filter(file -> Files.getFileExtension(file.getName()).equals("json"))
                    .map(MapSerializer::deserialize)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(mapList::add);
        } catch (NullPointerException e) {
            ClosedCombat.getInstance().getLogger().error("An error occurs while loading map properties files", e);
        }
    }

    public static List<Map> getMapList() {
        return mapList;
    }

    public static java.util.Map<String, Map> getMapMap() {
        return mapList.stream().collect(Collectors.toMap(Map::getName, Function.identity()));
    }
}
