package com.gmail.kazz96minecraft.utils;

import com.gmail.kazz96minecraft.utils.xnbt.XNBT;
import com.gmail.kazz96minecraft.utils.xnbt.types.CompoundTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WorldDatas {

    private final String mapName;
    private final File levelDat;
    private final File levelSpongeDat;
    private boolean levelDatExists = false;
    private boolean levelSpongeDatExists = false;
    private CompoundTag rootTag;
    private GeneratorType generatorType;

    public WorldDatas(String worldName) {
        this.mapName = worldName;
        String defaultWorldName = Sponge.getServer().getDefaultWorldName();

        if (defaultWorldName.equalsIgnoreCase(worldName)) {
            levelDat = new File(worldName, "level.dat");
            levelSpongeDat = new File(worldName, "level_sponge.dat");
        } else {
            levelDat = new File(defaultWorldName + File.separator + worldName, "level.dat");
            levelSpongeDat = new File(defaultWorldName + File.separator + worldName, "level_sponge.dat");
        }

        if (levelSpongeDat.exists()) {
            levelSpongeDatExists = true;
        }

        if (levelDat.exists()) {
            levelDatExists = true;
            init();
        }
    }

    private void init() {
        List<NBTTag> tagList;

        try {
            tagList = XNBT.loadTags(levelDat);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        tagList.forEach(root -> {
                    CompoundTag compoundRoot = (CompoundTag) root;

                    compoundRoot.entrySet().stream()
                            .filter(rootItem -> rootItem.getKey().equalsIgnoreCase("Data"))
                            .findFirst()
                            .ifPresent(rootItem -> rootTag = (CompoundTag) rootItem.getValue());
                }
        );

        initGeneratorType();
    }

    private void initGeneratorType() {
        final Optional<String> generatorName = rootTag.entrySet().stream()
                .filter(Objects::nonNull)
                .filter(tag -> tag.getKey().equalsIgnoreCase("generatorName"))
                .findFirst()
                .map(tag -> tag.getValue().getPayload().toString());

        if (!generatorName.isPresent()) {
            return;
        }

        Arrays.stream(CCGeneratorTypes.values())
                .filter(ccGeneratorTypes -> StringUtils.equalsIgnoreCase(generatorName.get(), ccGeneratorTypes.name()))
                .findFirst()
                .map(CCGeneratorTypes::getEquivalence)
                .ifPresent(generatorType -> this.generatorType = generatorType);
    }

    public String getMapName() {
        return mapName;
    }

    public boolean levelDatExists() {
        return levelDatExists;
    }

    public boolean levelSpongeDatExists() {
        return levelSpongeDatExists;
    }

    public GeneratorType getGeneratorType() {
        return generatorType;
    }

    private enum CCGeneratorTypes {
        AMPLIFIED(GeneratorTypes.AMPLIFIED),
        DEBUG(GeneratorTypes.DEBUG),
        DEFAULT(GeneratorTypes.DEFAULT),
        FLAT(GeneratorTypes.FLAT),
        LARGE_BIOMES(GeneratorTypes.LARGE_BIOMES),
        OVERWORLD(GeneratorTypes.OVERWORLD);

        private GeneratorType generatorTypeEquivalence;

        CCGeneratorTypes(GeneratorType generatorTypeEquivalence) {
            this.generatorTypeEquivalence = generatorTypeEquivalence;
        }

        private GeneratorType getEquivalence() {
            return generatorTypeEquivalence;
        }
    }
}
