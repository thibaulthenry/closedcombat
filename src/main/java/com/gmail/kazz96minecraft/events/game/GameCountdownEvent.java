package com.gmail.kazz96minecraft.events.game;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.Sponge;

public class GameCountdownEvent extends AbstractGameEvent {

    private final Integer countdown;

    private GameCountdownEvent(Game game, Integer countdown) {
        super(game);
        this.countdown = countdown;
    }

    public static void fire(Game game, Integer countdown) {
        GameCountdownEvent event = new GameCountdownEvent(game, countdown);
        Sponge.getEventManager().post(event);
    }

    public Integer getCountdown() {
        return countdown;
    }
}
