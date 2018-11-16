package com.gmail.kazz96minecraft.elements.serializers;

import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.utils.Storage;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapSerializer extends AbstractSerializer<Map> {

    private static final MapSerializer instance = new MapSerializer();

    private MapSerializer() {
        super(Map.class, Storage.mapsDirectory, new ArrayList<>(), Map::getName);
    }

    public static MapSerializer getInstance() {
        return instance;
    }

    public java.util.Map<String, Map> getHashMap() {
        return getList().stream().collect(Collectors.toMap(Map::getName, Function.identity()));
    }

    public Optional<Map> get(String mapName) {
        return getList().stream().filter(map -> StringUtils.equalsIgnoreCase(map.getName(), mapName)).findFirst();
    }

    public boolean removeRegisteredMap(Map map) {
        if (!getList().contains(map)) {
            return false;
        }

        getList().remove(map);
        return Storage.deleteMapFile(map);
    }
}
