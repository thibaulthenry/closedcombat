package com.gmail.kazz96minecraft.elements.serializers;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractSerializer<T> {

    private final File savingDirectory;
    private final Class<T> clazz;
    private final List<T> elementList;
    private final Function<T, String> jsonFileName;

    AbstractSerializer(Class<T> clazz, File savingDirectory, List<T> elementList, Function<T, String> jsonFileName) {
        this.clazz = clazz;
        this.savingDirectory = savingDirectory;
        this.elementList = elementList;
        this.jsonFileName = jsonFileName;
    }

    private Optional<T> deserialize(File file) {
        try (Reader reader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(reader)) {
            Gson gson = new Gson();
            T deserialized = gson.fromJson(bufferedReader, clazz);
            return Optional.of(deserialized);
        } catch (IOException e) {
            ClosedCombat.getInstance().getLogger().error("An error occurred while loading " + file, e);
            return Optional.empty();
        }
    }

    public boolean serialize(T serialized) {
        try (Writer writer = new FileWriter(Paths.get(savingDirectory.getPath(), jsonFileName.apply(serialized) + ".json").toString())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(serialized, writer);
            return true;
        } catch (IOException e) {
            ClosedCombat.getInstance().getLogger().error("An error occurred while saving " + serialized.toString() + "'s properties file", e);
            return false;
        }
    }

    public void load() {
        try {
            Arrays.stream(Objects.requireNonNull(savingDirectory.listFiles()))
                    .filter(file -> file.getName().contains(".json"))
                    .map(this::deserialize)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(elementList::add);
        } catch (NullPointerException e) {
            ClosedCombat.getInstance().getLogger().error("An error occurred while loading map properties files", e);
        }
    }

    public List<T> getList() {
        return elementList;
    }
}
