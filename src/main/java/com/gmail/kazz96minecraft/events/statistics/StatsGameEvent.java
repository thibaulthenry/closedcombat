package com.gmail.kazz96minecraft.events.statistics;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class StatsGameEvent extends AbstractStatsEvent {

    private StatsGameEvent(Player player) {
        super(player);
    }

    public static void fire(Player player) {
        StatsGameEvent event = new StatsGameEvent(player);
        Sponge.getEventManager().post(event);
    }
}
