package com.gmail.kazz96minecraft.listeners.player;

import com.gmail.kazz96minecraft.elements.Statistics;
import com.gmail.kazz96minecraft.events.statistics.StatsGameEvent;
import com.gmail.kazz96minecraft.events.statistics.StatsWinEvent;
import org.spongepowered.api.event.Listener;

@SuppressWarnings("unused")
public class StatsListener {

    @Listener
    public void onStatsGame(StatsGameEvent event) {
        Statistics.increase(event.getPlayer(), Statistics.Options.GAMES);
    }

    @Listener
    public void onStatsGame(StatsWinEvent event) {
        Statistics.increase(event.getPlayer(), Statistics.Options.WINS);
    }
}
