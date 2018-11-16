package com.gmail.kazz96minecraft.listeners.shifting;

import com.gmail.kazz96minecraft.elements.Game;
import com.gmail.kazz96minecraft.elements.Statistics;
import com.gmail.kazz96minecraft.elements.serializers.StatisticsSerializer;
import com.gmail.kazz96minecraft.utils.CCScoreboards;
import com.gmail.kazz96minecraft.utils.Shortcuts;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;

@SuppressWarnings("unused")
public class ConnectionListener {

    @Listener
    public void onPlayerConnect(ClientConnectionEvent.Join event, @First Player player) {
        event.setMessageCancelled(true);
        Shortcuts.runCommand("cc", "world", "teleport", Sponge.getServer().getDefaultWorldName(), player.getName());

        if (StatisticsSerializer.getInstance().get(player).isPresent()) {
            return;
        }

        Statistics statistics = new Statistics(player);

        if (StatisticsSerializer.getInstance().serialize(statistics)) {
            StatisticsSerializer.getInstance().getList().add(statistics);
        }

        CCScoreboards.setScoreboard(player);
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event, @First Player player) {
        event.setMessageCancelled(true);

        if (!Game.get(player).isPresent()) {
            return;
        }

        player.getInventory().clear();
        Game.get(player).get().leave(player);
    }

}
