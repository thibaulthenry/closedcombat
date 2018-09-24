package com.gmail.kazz96minecraft;

import com.gmail.kazz96minecraft.commands.CommandRegister;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import com.gmail.kazz96minecraft.elements.serializers.WarpSerializer;
import com.gmail.kazz96minecraft.listeners.ListenerRegister;
import com.gmail.kazz96minecraft.utils.PluginDetails;
import com.gmail.kazz96minecraft.utils.Storage;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@Plugin(
        id = PluginDetails.ID,
        name = PluginDetails.NAME,
        version = PluginDetails.VERSION,
        description = PluginDetails.DESC
)
public class ClosedCombat {

    private static ClosedCombat instance;

    @Inject
    private Logger logger;

    public static ClosedCombat getInstance() {
        return instance;
    }

    @Listener
    @SuppressWarnings("unused")
    public void onInitialization(GameInitializationEvent event) {
        instance = this;

        Sponge.getCommandManager().register(this, new CommandRegister().getCommandSpec(), "closedcombat", "cc");

        ListenerRegister.registerAll(this);

        Storage.init();
    }

    @Listener
    @SuppressWarnings("unused")
    public void onServerStarting(GameStartingServerEvent event) {
        sendConsole("Loading Closed Combat Maps...");
        MapSerializer.getInstance().load();

        sendConsole("Loading Closed Combat Signs...");
        WarpSerializer.getInstance().load();

        sendConsole("Verifying Signs existence...");
        WarpSerializer.getInstance().verifyRegisteredWarps();
    }

    public Logger getLogger() {
        return logger;
    }

    public void sendConsole(String text) {
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_GRAY, "[CC]: ", text));
    }
}