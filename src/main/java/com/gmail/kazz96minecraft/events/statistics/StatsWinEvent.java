package com.gmail.kazz96minecraft.events.statistics;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class StatsWinEvent extends AbstractStatsEvent {

    private StatsWinEvent(Player player) {
        super(player);
    }

    public static void fire(Player player) {
        StatsWinEvent event = new StatsWinEvent(player);
        Sponge.getEventManager().post(event);
    }
}
