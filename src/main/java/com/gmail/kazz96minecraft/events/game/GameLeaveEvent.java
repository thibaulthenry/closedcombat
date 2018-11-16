package com.gmail.kazz96minecraft.events.game;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.Sponge;

public class GameLeaveEvent extends AbstractGameEvent {

    private GameLeaveEvent(Game game) {
        super(game);
    }

    public static void fire(Game game) {
        GameLeaveEvent event = new GameLeaveEvent(game);
        Sponge.getEventManager().post(event);
    }
}
