package com.gmail.kazz96minecraft.events.game;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

@SuppressWarnings("NullableProblems")
public abstract class AbstractGameEvent extends AbstractEvent {

    private final Game game;

    AbstractGameEvent(Game game) {
        this.game = game;
    }

    @Override
    public Cause getCause() {
        return Cause.builder().build(EventContext.empty());
    }

    public Game getGame() {
        return game;
    }
}
