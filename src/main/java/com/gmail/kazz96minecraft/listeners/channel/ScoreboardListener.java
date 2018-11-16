package com.gmail.kazz96minecraft.listeners.channel;

import com.gmail.kazz96minecraft.utils.CCScoreboards;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

@SuppressWarnings("unused")
public class ScoreboardListener {

    @Listener
    public void onJoiningWorldLobby(MoveEntityEvent.Teleport event, @First Player player) {
        CCScoreboards.setScoreboard(player);
    }
}
