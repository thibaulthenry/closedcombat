package com.gmail.kazz96minecraft.listeners;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.listeners.block.CCStickListener;
import com.gmail.kazz96minecraft.listeners.channel.ChatListener;
import com.gmail.kazz96minecraft.listeners.shifting.ConnectionListener;
import org.spongepowered.api.Sponge;

public class ListenerRegister {

    public static void registerAll(ClosedCombat plugin) {
        Sponge.getEventManager().registerListeners(plugin, new CCStickListener());
        Sponge.getEventManager().registerListeners(plugin, new ChatListener());
        Sponge.getEventManager().registerListeners(plugin, new ConnectionListener());
    }

}
