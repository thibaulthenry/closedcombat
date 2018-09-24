package com.gmail.kazz96minecraft.listeners.shifting;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@SuppressWarnings("unused")
public class ConnectionListener {

    @Listener
    public void onPlayerConnect(ClientConnectionEvent.Join event, @First Player player) {
        event.setMessage(
                Text.builder()
                        .append(Text.of(TextColors.DARK_GRAY, "["))
                        .append(Text.of(TextColors.DARK_GREEN, "+"))
                        .append(Text.of(TextColors.DARK_GRAY, "] "))
                        .append(Text.of(TextColors.GRAY, player.getName()))
                        .build()
        );
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event, @First Player player) {
        event.setMessage(
                Text.builder()
                        .append(Text.of(TextColors.DARK_GRAY, "["))
                        .append(Text.of(TextColors.DARK_RED, "-"))
                        .append(Text.of(TextColors.DARK_GRAY, "] "))
                        .append(Text.of(TextColors.GRAY, player.getName()))
                        .build()
        );
    }

}
