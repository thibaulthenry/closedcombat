package com.gmail.kazz96minecraft.events.statistics;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

@SuppressWarnings("NullableProblems")
public abstract class AbstractStatsEvent extends AbstractEvent {

    private final Player player;

    AbstractStatsEvent(Player player) {
        this.player = player;
    }

    @Override
    public Cause getCause() {
        return Cause.builder().build(EventContext.empty());
    }

    public Player getPlayer() {
        return player;
    }
}
