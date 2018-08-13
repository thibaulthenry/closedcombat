package com.gmail.kazz96minecraft.listeners;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.listeners.block.CCStickListener;
import com.gmail.kazz96minecraft.listeners.channel.ChatListener;
import com.gmail.kazz96minecraft.listeners.shifting.ConnectionListener;
import org.spongepowered.api.Sponge;

import java.util.stream.Stream;

public class ListenerRegister {

    public static void registerAll(ClosedCombat plugin) {
        Stream.of(
                new CCStickListener(),
                new ChatListener(),
                new ConnectionListener()
        ).forEach(listener -> Sponge.getEventManager().registerListeners(plugin, listener));
    }

}
