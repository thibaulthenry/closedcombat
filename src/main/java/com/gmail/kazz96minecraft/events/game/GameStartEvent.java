package com.gmail.kazz96minecraft.events.game;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.Sponge;

public class GameStartEvent extends AbstractGameEvent {

    private GameStartEvent(Game game) {
        super(game);
    }

    public static void fire(Game game) {
        GameStartEvent event = new GameStartEvent(game);
        Sponge.getEventManager().post(event);
    }
}
