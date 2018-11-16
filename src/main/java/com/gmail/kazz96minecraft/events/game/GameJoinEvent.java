package com.gmail.kazz96minecraft.events.game;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.Sponge;

public class GameJoinEvent extends AbstractGameEvent {

    private GameJoinEvent(Game game) {
        super(game);
    }

    public static void fire(Game game) {
        GameJoinEvent event = new GameJoinEvent(game);
        Sponge.getEventManager().post(event);
    }
}
