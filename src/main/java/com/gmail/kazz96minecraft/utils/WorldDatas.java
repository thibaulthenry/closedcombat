package com.gmail.kazz96minecraft.utils;

import com.gmail.kazz96minecraft.ClosedCombat;
import net.obnoxint.xnbt.XNBT;
import net.obnoxint.xnbt.types.CompoundTag;
import net.obnoxint.xnbt.types.NBTTag;
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

    private final String worldName;
    private boolean levelDatExists = false;
    private boolean levelSpongeDatExists = false;
    private CompoundTag rootTag;
    private GeneratorType generatorType;

    public WorldDatas(String worldName) {
        this.worldName = worldName;
        String defaultWorldName = Sponge.getServer().getDefaultWorldName();

        File levelDat;
        File levelSpongeDat;
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
            init(levelDat);
        }
    }

    private void init(File levelDat) {
        List<NBTTag> tagList;

        try {
            tagList = XNBT.loadTags(levelDat);
        } catch (IOException e) {
            ClosedCombat.getInstance().getLogger().error("An error occurs while initializing " + worldName);
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

        private final GeneratorType generatorTypeEquivalence;

        CCGeneratorTypes(GeneratorType generatorTypeEquivalence) {
            this.generatorTypeEquivalence = generatorTypeEquivalence;
        }

        private GeneratorType getEquivalence() {
            return generatorTypeEquivalence;
        }
    }
}
