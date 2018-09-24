package com.gmail.kazz96minecraft.events.game;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.Sponge;

public class GameStopEvent extends AbstractGameEvent {

    private GameStopEvent(Game game) {
        super(game);
    }

    public static void fire(Game game) {
        GameStopEvent event = new GameStopEvent(game);
        Sponge.getEventManager().post(event);
    }
}
