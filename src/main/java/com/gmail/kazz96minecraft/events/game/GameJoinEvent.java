package com.gmail.kazz96minecraft.events.game;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class GameJoinEvent extends AbstractGameEvent {

    private final Player player;

    private GameJoinEvent(Game game, Player player) {
        super(game);
        this.player = player;
    }

    public static void fire(Game game, Player player) {
        GameJoinEvent event = new GameJoinEvent(game, player);
        Sponge.getEventManager().post(event);
    }

    public Player getPlayer() {
        return player;
    }
}
