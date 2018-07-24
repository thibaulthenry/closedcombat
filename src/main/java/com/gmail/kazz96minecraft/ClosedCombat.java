package com.gmail.kazz96minecraft;

import com.gmail.kazz96minecraft.commands.CCRegister;
import com.gmail.kazz96minecraft.utils.PluginDetails;
import com.gmail.kazz96minecraft.utils.Zip;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

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
    private PluginContainer plugin;

    public static ClosedCombat getInstance() {
        return instance;
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        plugin = Sponge.getPluginManager().getPlugin(PluginDetails.ID).orElseThrow(NoSuchFieldError::new);
        instance = this;

        Sponge.getCommandManager().register(this, new CCRegister().getCommandSpec(), "closedcombat", "cc");

        Zip.initBackuping();
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }
}