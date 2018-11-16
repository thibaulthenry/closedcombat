package com.gmail.kazz96minecraft.elements.serializers;

import com.gmail.kazz96minecraft.elements.Statistics;
import com.gmail.kazz96minecraft.utils.Storage;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Optional;

public class StatisticsSerializer extends AbstractSerializer<Statistics> {

    private static final StatisticsSerializer instance = new StatisticsSerializer();

    private StatisticsSerializer() {
        super(Statistics.class, Storage.statisticsDirectory, new ArrayList<>(), Statistics::getPlayerName);
    }

    public static StatisticsSerializer getInstance() {
        return instance;
    }

    public Optional<Statistics> get(Player player) {
        return getList().stream().filter(statistics -> statistics.getPlayerName().equals(player.getName())).findFirst();
    }
}
