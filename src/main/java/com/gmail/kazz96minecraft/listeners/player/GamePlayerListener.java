package com.gmail.kazz96minecraft.listeners.player;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

@SuppressWarnings("unused")
public class GamePlayerListener {

    @Listener
    public void onGamePlayerDying(DestructEntityEvent.Death event, @First Player player) {
        if (!Game.get(player).isPresent()) {
            return;
        }

        Game game = Game.get(player).get();
        player.getInventory().clear();
        game.leave(player);
        Sponge.getCommandManager().process(player, "cc world tp " + Sponge.getServer().getDefaultWorldName());
        event.setCancelled(true);
        player.respawnPlayer();
    }
}
