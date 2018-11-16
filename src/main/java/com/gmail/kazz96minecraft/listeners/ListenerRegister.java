package com.gmail.kazz96minecraft.listeners;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.listeners.block.CCItemsListener;
import com.gmail.kazz96minecraft.listeners.block.CCSignListener;
import com.gmail.kazz96minecraft.listeners.block.GameBlockListener;
import com.gmail.kazz96minecraft.listeners.channel.ChatListener;
import com.gmail.kazz96minecraft.listeners.channel.ScoreboardListener;
import com.gmail.kazz96minecraft.listeners.player.GamePlayerListener;
import com.gmail.kazz96minecraft.listeners.player.PlayerListener;
import com.gmail.kazz96minecraft.listeners.player.StatsListener;
import com.gmail.kazz96minecraft.listeners.shifting.ConnectionListener;
import org.spongepowered.api.Sponge;

import java.util.stream.Stream;

public class ListenerRegister {

    public static void registerAll() {
        Stream.of(
                new CCItemsListener(),
                new CCSignListener(),
                new GameBlockListener(),
                new ChatListener(),
                new ScoreboardListener(),
                new GamePlayerListener(),
                new PlayerListener(),
                new StatsListener(),
                new ConnectionListener()
        ).forEach(listener -> Sponge.getEventManager().registerListeners(ClosedCombat.getInstance(), listener));
    }

}
