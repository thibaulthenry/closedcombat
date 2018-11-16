package com.gmail.kazz96minecraft.listeners.player;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

@SuppressWarnings("unused")
public class PlayerListener {

    @Listener
    public void onBreakOrPlace(ChangeBlockEvent event, @First Player player) {
        if (player.hasPermission("closedcombat.map")) {
            return;
        }

        if (Game.get(player).isPresent()) {
            return;
        }

        event.setCancelled(true);
    }
}
