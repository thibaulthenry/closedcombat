package com.gmail.kazz96minecraft.listeners;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.listeners.block.CCItemsListener;
import com.gmail.kazz96minecraft.listeners.block.CCSignListener;
import com.gmail.kazz96minecraft.listeners.block.GameBlockListener;
import com.gmail.kazz96minecraft.listeners.channel.ChatListener;
import com.gmail.kazz96minecraft.listeners.player.GamePlayerListener;
import com.gmail.kazz96minecraft.listeners.shifting.ConnectionListener;
import org.spongepowered.api.Sponge;

import java.util.stream.Stream;

public class ListenerRegister {

    public static void registerAll(ClosedCombat plugin) {
        Stream.of(
                new CCItemsListener(),
                new CCSignListener(),
                new GameBlockListener(),
                new ChatListener(),
                new GamePlayerListener(),
                new ConnectionListener()
        ).forEach(listener -> Sponge.getEventManager().registerListeners(plugin, listener));
    }

}
